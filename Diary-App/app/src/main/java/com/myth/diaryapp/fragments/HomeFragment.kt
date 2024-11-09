package com.myth.diaryapp.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.myth.diaryapp.MainActivity
import com.myth.diaryapp.R
import com.myth.diaryapp.adapter.RecordsAdapter
import com.myth.diaryapp.databinding.FragmentHomeBinding
import com.myth.diaryapp.model.DiaryRecord
import com.myth.diaryapp.viewmodel.RecordViewModel
import androidx.navigation.fragment.findNavController
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment(), SearchView.OnQueryTextListener {
    private var _binding : FragmentHomeBinding? = null
    private val binding get() = _binding

    private lateinit var recordsViewModel: RecordViewModel
    private lateinit var recordsAdapter: RecordsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordsViewModel = (activity as MainActivity).recordViewModel

        setUpRecyclerView()
        binding?.fabAddRecord!!.setOnClickListener{
            it.findNavController().navigate(
                R.id.action_homeFragment_to_newRecordFragment
            )
        }
    }


    private fun setUpRecyclerView(){
        recordsAdapter = RecordsAdapter()

        binding?.recyclerView?.apply {
            layoutManager = LinearLayoutManager((activity as MainActivity).applicationContext)
            adapter = recordsAdapter
        }

        activity?.let {
            recordsViewModel.getAllRecords().observe(
                viewLifecycleOwner
            ){record ->
                recordsAdapter.differ.submitList(record)
                updateUI(record)
            }
        }
    }


    private fun updateUI(record : List<DiaryRecord>){
        if(record != null){
            if(record.isNotEmpty()){
                binding?.cardView!!.visibility = View.GONE
                binding?.recyclerView!!.visibility = View.VISIBLE
            }else{
                binding?.cardView!!.visibility = View.VISIBLE
                binding?.recyclerView!!.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.home_menu, menu)

        val mMenuSearch = menu.findItem(R.id.menu_search).actionView as SearchView

        mMenuSearch.isSubmitButtonEnabled = false
        mMenuSearch.setOnQueryTextListener(this)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!=null){
            searchNote(query)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(newText != null){
            searchNote(newText)
        }
        return true
    }

    private fun searchNote(query: String){
        val searchQuery = query

        recordsViewModel.searchRecords(searchQuery).observe(
            viewLifecycleOwner
        ){list->recordsAdapter.differ.submitList(list)}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_reminder -> {
                findNavController().navigate(R.id.action_homeFragment_to_reminderFragment)
                return true
            }
            R.id.menu_filter -> {
                showDatePickerDialog() // Show date picker when filter is pressed
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }

            // Format the selected date to match the stored format
            val dateFormat = SimpleDateFormat("EEEE d MMMM yyyy", Locale.getDefault())
            val selectedFormattedDate = dateFormat.format(selectedDate.time)

            // Now filter records for that date. You'll need to adjust your filtering logic.
            filterRecordsByDate(selectedFormattedDate)
        }, year, month, day).show()
    }

    private fun filterRecordsByDate(selectedDate: String) {
        recordsViewModel.getAllRecords().observe(viewLifecycleOwner) { allRecords ->
            // Filter records based on whether their timestamp contains the selected date
            val filteredRecords = allRecords.filter { record ->
                record.recordTimestamp.contains(selectedDate)
            }
            recordsAdapter.differ.submitList(filteredRecords)
            updateUI(filteredRecords)
        }
    }





    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}




