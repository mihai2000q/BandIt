package com.bandit.ui.songs.albums

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.databinding.DialogFragmentAlbumDetailBinding
import com.bandit.extension.printMinutesAndSeconds
import com.bandit.extension.printName
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.helper.TouchHelper
import com.bandit.ui.songs.SongAddDialogFragment
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

class AlbumDetailDialogFragment(
    private val onClickSong: (Song) -> Unit,
    private val onEditSong: (Song) -> Unit,
    private val onEditAlbum: (Album) -> Unit,
    private val onDeleteAlbum: (Album) -> Unit
) : DialogFragment() {

    private var _binding: DialogFragmentAlbumDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentAlbumDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        val albumAddSongDialogFragment = AlbumAddSongDialogFragment()
        with(binding) {
            albumDetailBtAddSongs.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    albumAddSongDialogFragment,
                    childFragmentManager
                )
            }
            val touchHelper = TouchHelper<Song>(
                super.requireContext(),
                albumDetailRvSongList,
                { song -> onRemoveFromAlbum(song) },
                { song -> onEditSong(song) }
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(albumDetailRvSongList)
            viewModel.albums.observe(viewLifecycleOwner) {
                val album = it.first { a -> a.id == viewModel.selectedAlbum.value!!.id }
                this@AlbumDetailDialogFragment.assignDetails(album)
                if(album.songs.isEmpty()) {
                    albumDetailRvEmpty.visibility = View.VISIBLE
                    albumDetailRvSongList.visibility = View.GONE
                }
                else {
                    albumDetailRvEmpty.visibility = View.GONE
                    albumDetailRvSongList.visibility = View.VISIBLE
                    touchHelper.updateItems(album.songs.sorted().reversed())
                    albumDetailRvSongList.adapter =
                        SongAdapter(
                            this@AlbumDetailDialogFragment,
                            album.songs.sorted().reversed(),
                            viewModel,
                            { song -> onRemoveFromAlbum(song) },
                            { song -> onEditSong(song) },
                            { song -> onClickSong.invoke(song) },
                            resources.getString(R.string.album_remove_from_album)
                        )
                }
            }
            val songAddDialogFragment = SongAddDialogFragment(viewModel.selectedAlbum.value!!)
            albumDetailBtAddNewSong.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    songAddDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    private fun assignDetails(album: Album) {
        with(binding) {
            albumDetailTvAlbumName.text = album.name
            albumDetailReleaseDate.text = album.releaseDate.printName()
            albumDetailDuration.text = album.duration.printMinutesAndSeconds()
            AndroidUtils.ifNullHide(albumDetailLabel, albumDetailTvLabel, album.label)
            albumDetailBtDelete.setOnClickListener {
                onDeleteAlbum.invoke(album)
                super.dismiss()
            }
            albumDetailBtEdit.setOnClickListener {
                onEditAlbum.invoke(album)
                super.dismiss()
            }
        }
    }

    private fun onRemoveFromAlbum(song: Song) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.song_from_album_alert_dialog_title),
            resources.getString(R.string.song_from_album_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                this@AlbumDetailDialogFragment) {
                viewModel.removeSongFromAlbum(viewModel.selectedAlbum.value!!, song)
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.album_remove_song_toast),
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Song.Album.DETAIL_TAG
    }
}