package com.bandit.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.extension.StringExtensions.normalizeWord
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class SongAddDialogFragment : SongDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_add)
            songButton.setOnClickListener {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    songButton
                )
                viewModel.addSong(
                    songEtName.text.toString(),
                    ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    songEtAlbumName.text.toString().normalizeWord(),
                    ParserUtils.parseDuration(songEtDuration.text.toString())
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.song_add_toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.ADD_TAG
    }
}