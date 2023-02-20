package com.bandit.component

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.databinding.DialogImagePickerBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImagePickerDialog(
    private val profilePicture: ImageView,
    private val savePic: suspend (Uri) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: DialogImagePickerBinding? = null
    private val binding get() = _binding!!
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogImagePickerBinding.inflate(layoutInflater, container, false)
        galleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if(it.resultCode == Activity.RESULT_OK) {
                AndroidUtils.loadIntent(this@ImagePickerDialog) {
                    loadProfilePic(it.data?.data)
                }
            }
        }
        cameraLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) {
            if(it.resultCode == Activity.RESULT_OK) {
                AndroidUtils.loadIntent(this@ImagePickerDialog) {
                    //TODO: Replace deprecated method for .get() as Bitmap
                    loadProfilePic(AndroidUtils.getImageUri(super.requireContext(),
                        it.data?.extras?.get("data") as Bitmap))
                }
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissionChecker = DILocator.getPermissionChecker(super.requireActivity())
        with(binding) {
            imagePickerTvCamera.setOnClickListener {
                if(permissionChecker.checkCameraPermission())
                    camera()
                else
                    permissionChecker.requestCameraPermission()
            }
            imagePickerTvGallery.setOnClickListener {
                if(permissionChecker.checkReadStoragePermission())
                    gallery()
                else
                    permissionChecker.requestReadStoragePermission()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun camera() {
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(camera)
    }

    private fun gallery() {
        val gallery = Intent()
        gallery.type = "image/+"
        gallery.action = Intent.ACTION_GET_CONTENT
        galleryLauncher.launch(gallery)
    }

    private suspend fun loadProfilePic(uri: Uri?) {
        if (uri != null)
            savePic(uri)
        Glide.with(super.requireActivity())
            .load(uri)
            .placeholder(R.drawable.placeholder_profile_pic)
            .into(profilePicture)
    }

    companion object {
        const val TAG = Constants.Component.IMAGE_PICKER_DIALOG_TAG
    }
}