package com.bandit.ui.songs

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class SongFilterDialogFragment : SongDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_filter)
            songButton.setOnClickListener {
                viewModel.filterSongs(
                    songEtName.text.toString(),
                    if(songEtReleaseDate.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    songEtAlbumName.text.toString(),
                    if(songEtDuration.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDuration(songEtDuration.text.toString())
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.song_filter_toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.FILTER_TAG
    }
}