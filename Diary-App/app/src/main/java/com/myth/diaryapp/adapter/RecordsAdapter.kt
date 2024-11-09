package com.myth.diaryapp.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myth.diaryapp.R
import com.myth.diaryapp.databinding.RecordLayoutBinding
import com.myth.diaryapp.fragments.HomeFragmentDirections
import com.myth.diaryapp.model.DiaryRecord

class RecordsAdapter : RecyclerView.Adapter<RecordsAdapter.RecordsViewHolder>() {

    class RecordsViewHolder(val itemBinding: RecordLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<DiaryRecord>() {
        override fun areContentsTheSame(oldItem: DiaryRecord, newItem: DiaryRecord): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(oldItem: DiaryRecord, newItem: DiaryRecord): Boolean {
            return oldItem.id == newItem.id
                    && oldItem.recordBody == newItem.recordBody
                    && oldItem.recordTitle == newItem.recordTitle
                    && oldItem.recordTimestamp == newItem.recordTimestamp
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsViewHolder {
        return RecordsViewHolder(
            RecordLayoutBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecordsViewHolder, position: Int) {
        val currentRecord = differ.currentList[position]

        holder.itemBinding.tvRecordTitle.text = currentRecord.recordTitle
        holder.itemBinding.tvRecordBody.text = currentRecord.recordBody
        holder.itemBinding.tvNoteDate.text = currentRecord.recordTimestamp.toString()

        holder.itemView.setOnClickListener{
            val direction = HomeFragmentDirections.actionHomeFragmentToUpdateRecordFragment(currentRecord)
            it.findNavController().navigate(direction)
        }

    }



    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}