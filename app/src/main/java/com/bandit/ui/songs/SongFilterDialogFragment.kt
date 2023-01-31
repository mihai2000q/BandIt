package com.bandit.ui.songs

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.util.ParserUtils

class SongFilterDialogFragment : SongDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_filter)
            songButton.setOnClickListener {
                viewModel.filterSongs(
                    songEtName.text.toString(),
                    ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    songEtAlbumName.text.toString(),
                    ParserUtils.parseDuration(songEtDuration.text.toString())
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.FILTER_TAG
    }
}