package com.bandit.ui.component

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
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
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var galleryPermissionLauncher: ActivityResultLauncher<String>
    private val permissionService by lazy { DILocator.getPermissionService(super.requireActivity()) }

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
        cameraPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if(it)
                this.camera()
            else
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.permission_denied_toast)
                )
        }
        galleryPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if(it)
                this.gallery()
            else
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.permission_denied_toast)
                )
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            imagePickerTvCamera.setOnClickListener {
                permissionService.checkPermission(Manifest.permission.CAMERA,
                    cameraPermissionLauncher) { this@ImagePickerDialog.camera() }
            }
            imagePickerTvGallery.setOnClickListener {
                val permission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                    Manifest.permission.READ_MEDIA_IMAGES
                else
                    Manifest.permission.READ_EXTERNAL_STORAGE
                permissionService.checkPermission(permission,
                    galleryPermissionLauncher) { this@ImagePickerDialog.gallery() }
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