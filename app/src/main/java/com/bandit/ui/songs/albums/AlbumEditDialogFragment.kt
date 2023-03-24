package com.bandit.ui.songs.albums

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.template.AlbumDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class AlbumEditDialogFragment : AlbumDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            albumButton.setText(R.string.bt_save)
            viewModel.selectedAlbum.observe(viewLifecycleOwner) {
                albumEtName.setText(it.name)
                albumEtReleaseDate.setText(it.releaseDate.toString())
                albumEtLabel.setText(it.label)
            }
            albumButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@AlbumEditDialogFragment) { editAlbum() }
            }
        }
    }

    override fun validateFields(): Boolean {
        val result = super.validateFields()
        with(binding) {
            with(viewModel.selectedAlbum.value!!) {
                if (albumEtName.text.toString() == name &&
                    albumEtReleaseDate.text.toString() == releaseDate.toString() &&
                    albumEtLabel.text.toString() == label
                ) {
                    albumEtName.error = resources.getString(R.string.nothing_changed_validation)
                    return false
                }
            }
        }
        return result
    }

    private suspend fun editAlbum() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                albumEtName
            )
            viewModel.editAlbum(
                Album(
                    albumEtName.text.toString(),
                    viewModel.selectedAlbum.value!!.bandId,
                    ParserUtils.parseDate(albumEtReleaseDate.text.toString()),
                    albumEtLabel.text.toString(),
                    viewModel.selectedAlbum.value!!.songs,
                    viewModel.selectedAlbum.value!!.id
                )
            )
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.album_edit_toast)
            )
            super.dismiss()
        }
    }

    companion object {
        const val TAG = Constants.Song.Album.EDIT_TAG
    }
}