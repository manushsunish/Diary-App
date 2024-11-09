package com.myth.diaryapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.myth.diaryapp.MainActivity
import com.myth.diaryapp.R
import com.myth.diaryapp.databinding.FragmentUpdateRecordBinding
import com.myth.diaryapp.model.DiaryRecord
import com.myth.diaryapp.viewmodel.RecordViewModel

class UpdateRecordFragment : Fragment() {

    private var _binding: FragmentUpdateRecordBinding? = null
    private val binding get() = _binding

    private lateinit var recordViewModel: RecordViewModel
    private lateinit var currentRecord: DiaryRecord

    private val PICK_IMAGE_REQUEST = 1
    private val PICK_VIDEO_REQUEST = 2

    private var imageUri: Uri? = null
    private var videoUri: Uri? = null

    private val args: UpdateRecordFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpdateRecordBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recordViewModel = (activity as MainActivity).recordViewModel
        currentRecord = args.record!!

        // Set the title and body from the current record
        binding?.etRecordBodyUpdate?.setText(currentRecord.recordBody)
        binding?.etRecordTitleUpdate?.setText(currentRecord.recordTitle)

        // Load media (image/video) if available
        loadMedia(currentRecord)

        // Done button click listener
        binding?.fabDone!!.setOnClickListener {
            val title = binding?.etRecordTitleUpdate!!.text.toString().trim()
            val body = binding?.etRecordBodyUpdate!!.text.toString().trim()

            if (title.isNotEmpty()) {
                val record = DiaryRecord(currentRecord.id, title, body, currentRecord.recordTimestamp, currentRecord.recordImageUri, currentRecord.recordVideoUri)

                recordViewModel.updateRecord(record)
                view.findNavController().navigate(R.id.action_updateRecordFragment_to_homeFragment)
            } else {
                Toast.makeText(context, "Please enter the title", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun loadMedia(record: DiaryRecord) {
        // Check for an image URI and display it
        record.recordImageUri?.let { imageUri ->
            binding?.ivRecordImage?.apply {
                visibility = View.VISIBLE
                setImageURI(Uri.parse(imageUri)) // Convert string to Uri
            }
        } ?: run {
            binding?.ivRecordImage?.visibility = View.GONE // Hide image if not present
        }

        // Check for a video URI and display it
        record.recordVideoUri?.let { videoUri ->
            binding?.vvRecordVideo?.apply {
                visibility = View.VISIBLE
                setVideoURI(Uri.parse(videoUri)) // Convert string to Uri
                val mediaController = MediaController(context)
                mediaController.setAnchorView(this) // Set MediaController to the VideoView
                setMediaController(mediaController)
                start() // Start the video if you want it to play automatically
            }
        } ?: run {
            binding?.vvRecordVideo?.visibility = View.GONE // Hide video if not present
        }
    }

    private fun deleteRecord() {
        AlertDialog.Builder(activity).apply {
            setTitle("Delete diary entry?")
            setMessage("Are you sure you want to delete this entry?")
            setPositiveButton("Delete") { _, _ ->
                recordViewModel.deleteRecord(currentRecord)
                view?.findNavController()?.navigate(R.id.action_updateRecordFragment_to_homeFragment)
            }
            setNegativeButton("Cancel", null)
        }.create().show()
    }

    private fun showMediaSelectionDialog() {
        val options = arrayOf("Image", "Video")
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
        imageUri = null
        videoUri = null

        // Clear the URIs from currentRecord
        currentRecord.recordImageUri = null
        currentRecord.recordVideoUri = null

        // Hide both ImageView and VideoView
        binding?.ivRecordImage?.visibility = View.GONE
        binding?.vvRecordVideo?.visibility = View.GONE

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


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.update_record_xml, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> {
                deleteRecord()
            }
            R.id.menu_reminder -> {
                // Clear media (image or video) when this icon is clicked
                removeMedia()
            }
            R.id.menu_add_image -> {
                // Show a dialog to choose between image or video
                showMediaSelectionDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    imageUri = data.data
                    currentRecord.recordImageUri = imageUri.toString() // Update the current record
                    binding?.ivRecordImage?.setImageURI(imageUri)
                    binding?.ivRecordImage?.visibility = View.VISIBLE
                    binding?.vvRecordVideo?.visibility = View.GONE
                    videoUri = null // Clear videoUri when an image is selected
                }
                PICK_VIDEO_REQUEST -> {
                    videoUri = data.data
                    currentRecord.recordVideoUri = videoUri.toString()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
