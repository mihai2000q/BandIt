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
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.databinding.DialogFragmentAlbumDetailBinding
import com.bandit.extension.print
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.helper.TouchHelper
import com.bandit.ui.songs.SongAddDialogFragment
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

class AlbumDetailDialogFragment(
    private val onEditSong: (Song) -> Unit,
    private val onRemoveAlbum: (Album) -> Unit
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
            val onRemoveFromAlbum = { song: Song ->
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
            albumDetailBtAddSongs.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    albumAddSongDialogFragment,
                    childFragmentManager
                )
            }
            val touchHelper = TouchHelper(
                super.requireContext(),
                albumDetailRvSongList,
                onRemoveFromAlbum,
                { song -> onEditSong(song) }
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(albumDetailRvSongList)
            viewModel.albums.observe(viewLifecycleOwner) {
                val album = it.first { a -> viewModel.selectedAlbum.value?.id == a.id }
                albumDetailTvAlbumName.text = album.name
                albumDetailDuration.text = album.duration.print()
                touchHelper.updateItems(album.songs.sorted().reversed())
                albumDetailRvSongList.adapter =
                    SongAdapter(
                        this@AlbumDetailDialogFragment,
                        album.songs.sorted().reversed(),
                        viewModel,
                        onRemoveFromAlbum,
                        { song -> onEditSong(song) },
                        resources.getString(R.string.album_remove_from_album)
                    )
            }
            val songAddDialogFragment = SongAddDialogFragment(viewModel.selectedAlbum.value!!)
            albumDetailBtAddNewSong.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    songAddDialogFragment,
                    childFragmentManager
                )
            }
            albumDetailBtDelete.setOnClickListener {
                onRemoveAlbum.invoke(viewModel.selectedAlbum.value!!)
                super.dismiss()
            }
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