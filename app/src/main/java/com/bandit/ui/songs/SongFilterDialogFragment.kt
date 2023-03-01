package com.bandit.ui.songs

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.ui.template.SongDialogFragment
import com.bandit.util.ParserUtils
import com.google.android.material.badge.BadgeDrawable

class SongFilterDialogFragment(private val badgeDrawable: BadgeDrawable) : SongDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val map = mapOf(
                songEtName to SongsViewModel.SongFilter.Name,
                songEtReleaseDate to SongsViewModel.SongFilter.ReleaseDate,
                songEtDuration to SongsViewModel.SongFilter.Duration,
            )
            map.forEach { (key, value) -> key.setText(viewModel.songFilters.value?.get(value)) }
            songButton.setText(R.string.bt_filter)
            songButton.setOnClickListener {
                viewModel.filterSongs(
                    songEtName.text.toString(),
                    if(songEtReleaseDate.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDate(songEtReleaseDate.text.toString()),
                    null,
                    if(songEtDuration.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDurationText(songEtDuration.text.toString())
                )
                map.forEach { (key, value) ->
                    viewModel.songFilters.value?.replace(value, key.text.toString())
                }
                badgeDrawable.number = viewModel.getSongFiltersOn()
                badgeDrawable.isVisible = viewModel.getSongFiltersOn() > 0
                AndroidComponents.toastNotification(
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