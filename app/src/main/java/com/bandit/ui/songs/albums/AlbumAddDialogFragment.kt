package com.bandit.ui.songs.albums

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class AlbumAddDialogFragment : AlbumDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            albumButton.setText(R.string.bt_add)
            albumButton.setOnClickListener {
                if (validateFields())
                    addAlbum()
            }
        }
    }

    private fun addAlbum() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                albumButton
            )
            viewModel.addAlbum(
                Album(
                    albumEtName.text.toString(),
                    DILocator.database.currentBand.id,
                    ParserUtils.parseDate(albumEtReleaseDate.text.toString()),
                    albumEtLabel.text.toString()
                )
            )
            AndroidUtils.toastNotification(
                super.requireContext(),
                resources.getString(R.string.album_add_toast)
            )
            super.dismiss()
            albumEtName.setText("")
            albumEtReleaseDate.setText("")
            albumEtLabel.setText("")
        }
    }

    companion object {
        const val TAG = Constants.Song.Album.ADD_TAG
    }
}