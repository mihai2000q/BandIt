package com.bandit.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.data.model.Song
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class SongAddDialogFragment : SongDialogFragment() {
    private val _database = DILocator.database

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_add)
            songButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadIntent(this@SongAddDialogFragment) { addSong() }
            }
        }
    }

    private suspend fun addSong() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                songButton
            )
            viewModel.addSong(
                Song(
                    songEtName.text.toString(),
                    _database.currentBand.id,
                    ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    ParserUtils.parseDuration(songEtDuration.text.toString())
                )
            )
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.song_add_toast)
            )
            super.dismiss()
            songEtName.setText("")
            songEtReleaseDate.setText("")
            songEtDuration.setText("")
        }
    }

    companion object {
        const val TAG = Constants.Song.ADD_TAG
    }
}