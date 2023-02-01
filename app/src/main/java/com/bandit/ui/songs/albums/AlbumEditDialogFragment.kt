package com.bandit.ui.songs.albums

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Album
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
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.Album.EDIT_TAG
    }
}