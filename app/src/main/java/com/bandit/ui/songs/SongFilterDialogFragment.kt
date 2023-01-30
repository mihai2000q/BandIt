package com.bandit.ui.songs

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.util.AndroidUtils

class SongFilterDialogFragment : SongDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_filter)
            songButton.setOnClickListener {
                viewModel.filterSongs(
                    songEtName.text.toString(),
                    AndroidUtils.parseDate(songEtReleaseDate),
                    songEtAlbumName.text.toString(),
                    AndroidUtils.parseDuration(songEtDuration)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.FILTER_TAG
    }
}