package com.myth.diaryapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.myth.diaryapp.MainActivity
import com.myth.diaryapp.R
import com.myth.diaryapp.databinding.FragmentNewRecordBinding
import com.myth.diaryapp.model.DiaryRecord
import com.myth.diaryapp.viewmodel.RecordViewModel
import java.text.SimpleDateFormat
import java.util.*

class NewRecordFragment : Fragment() {
    private var _binding: FragmentNewRecordBinding? = null
    private val binding get() = _binding

    private lateinit var recordsViewModel: RecordViewModel
    private lateinit var mView: View

    // Constants for selecting media
    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 2

    private var imageUri: Uri? = null
    private var videoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewRecordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordsViewModel = (activity as MainActivity).recordViewModel
        mView = view
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_add_image -> {
                // Show a dialog to choose between image or video
                showMediaSelectionDialog()
            }
            R.id.menu_save -> {
                saveRecord(mView)
            }
            R.id.menu_reminder -> {
                // Clear media (image or video) when this icon is clicked
                removeMedia()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showMediaSelectionDialog() {
        val options = arrayOf("Image", "Video")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Media Type")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> selectImage() // If Image is selected
                1 -> selectVideo() // If Video is selected
            }
        }
        builder.show()
    }

    private fun removeMedia() {
        // Clear image and video URIs
        imageUri = null
        videoUri = null

        // Hide both ImageView and VideoView
        binding?.ivRecordImage?.visibility = View.GONE
        binding?.vvRecordVideo?.visibility = View.GONE

        // Show a message indicating media has been removed
        Toast.makeText(requireContext(), "Media removed", Toast.LENGTH_SHORT).show()
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun selectVideo() {
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data.data
                    videoUri = null // Clear video URI if an image is selected
                    binding?.ivRecordImage?.setImageURI(imageUri)
                    binding?.ivRecordImage?.visibility = View.VISIBLE
                    binding?.vvRecordVideo?.visibility = View.GONE
                }
                PICK_VIDEO_REQUEST -> {
                    videoUri = data.data
                    imageUri = null // Clear image URI if a video is selected
                    binding?.vvRecordVideo?.apply {
                        setVideoURI(videoUri)
                        visibility = View.VISIBLE
                        binding?.ivRecordImage?.visibility = View.GONE

                        // Add media controls for play/pause
                        val mediaController = MediaController(requireContext())
                        mediaController.setAnchorView(this)
                        setMediaController(mediaController)

                        // Start video playback
                        start()
                    }
                }
            }
        }
    }

    private fun saveRecord(view: View) {
        val recordTitle = binding?.etRecordTitle!!.text.toString().trim()
        val recordBody = binding?.etRecordBody!!.text.toString().trim()
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("HHmm EEEE d MMMM yyyy", Locale.getDefault())
        val recordTimestamp = sdf.format(currentTime).toString()

        if (recordTitle.isNotEmpty()) {
            val diaryRecord = DiaryRecord(
                0,
                recordTitle,
                recordBody,
                recordTimestamp,
                imageUri?.toString(), // Save image URI if exists
                videoUri?.toString()  // Save video URI if exists
            )

            recordsViewModel.addRecord(diaryRecord)

            Toast.makeText(mView.context, "Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().navigate(R.id.action_newRecordFragment_to_homeFragment)
        } else {
            Toast.makeText(mView.context, "Please enter title", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.newrecord_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
