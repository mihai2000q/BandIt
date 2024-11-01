package com.bandit.ui.songs.albums

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.songs.SongsViewModel
import com.bandit.ui.template.AlbumDialogFragment
import com.bandit.util.ParserUtils
import com.google.android.material.badge.BadgeDrawable

class AlbumFilterDialogFragment(private val badgeDrawable: BadgeDrawable) : AlbumDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val map = mapOf(
                albumEtName to SongsViewModel.AlbumFilter.Name,
                albumEtReleaseDate to SongsViewModel.AlbumFilter.ReleaseDate,
                albumEtLabel to SongsViewModel.AlbumFilter.Label,
            )
            map.forEach { (key, value) -> key.setText(viewModel.albumFilters.value?.get(value)) }
            albumButton.setText(R.string.bt_filter)
            albumButton.setOnClickListener {
                viewModel.filterAlbums(
                    albumEtName.text.toString(),
                    if(albumEtReleaseDate.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDate(albumEtReleaseDate.text.toString()),
                    albumEtLabel.text.toString()
                )
                map.forEach { (key, value) ->
                    viewModel.albumFilters.value?.replace(value, key.text.toString())
                }
                badgeDrawable.number = viewModel.getAlbumFiltersOn()
                badgeDrawable.isVisible = viewModel.getAlbumFiltersOn() > 0
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.album_filter_toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Song.Album.FILTER_TAG
    }
}