package com.bandit.ui.songs.albums

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.template.AlbumDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class AlbumAddDialogFragment : AlbumDialogFragment() {
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            albumButton.setText(R.string.bt_add)
            albumButton.setOnClickListener {
                if (validateFields())
                    AndroidUtils.loadDialogFragment(this@AlbumAddDialogFragment) { addAlbum() }
            }
        }
    }

    private suspend fun addAlbum() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                albumButton
            )
            viewModel.addAlbum(
                Album(
                    albumEtName.text.toString(),
                    bandViewModel.band.value!!.id,
                    ParserUtils.parseDate(albumEtReleaseDate.text.toString()),
                    albumEtLabel.text.toString()
                )
            )
            AndroidComponents.toastNotification(
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