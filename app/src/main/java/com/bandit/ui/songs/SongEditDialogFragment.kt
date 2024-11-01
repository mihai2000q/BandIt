package com.bandit.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Song
import com.bandit.extension.printMinutesAndSeconds
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.template.SongDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class SongEditDialogFragment : SongDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            songButton.setText(R.string.bt_save)
            viewModel.selectedSong.observe(viewLifecycleOwner) {
                songEtName.setText(it.name)
                songEtReleaseDate.setText(it.releaseDate.toString())
                songEtDuration.setText(it.duration.printMinutesAndSeconds())
            }
            songButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@SongEditDialogFragment) { editSong() }
            }
        }
    }

    override fun validateFields(): Boolean {
        val result = super.validateFields()
        with(binding) {
            with(viewModel.selectedSong.value!!) {
                if (songEtName.text.toString() == name &&
                    songEtReleaseDate.text.toString() == releaseDate.toString() &&
                    songEtDuration.text.toString() == duration.printMinutesAndSeconds()
                ) {
                    songEtName.error = resources.getString(R.string.nothing_changed_validation)
                    return false
                }
            }
        }
        return result
    }

    private suspend fun editSong() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                songEtName
            )
            viewModel.editSong(
                Song(
                    songEtName.text.toString(),
                    viewModel.selectedSong.value!!.bandId,
                    ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    ParserUtils.parseDurationTextToMinutesAndSeconds(songEtDuration.text.toString()),
                    viewModel.selectedSong.value!!.albumName,
                    viewModel.selectedSong.value!!.albumId,
                    viewModel.selectedSong.value!!.id
                )
            )
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.song_edit_toast)
            )
            super.dismiss()
        }
    }

    companion object {
        const val TAG = Constants.Song.EDIT_TAG
    }
}