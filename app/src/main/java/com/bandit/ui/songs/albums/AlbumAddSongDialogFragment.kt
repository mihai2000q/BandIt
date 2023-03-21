package com.bandit.ui.songs.albums

import android.app.ActionBar
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Song
import com.bandit.databinding.DialogFragmentAlbumAddSongBinding
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

class AlbumAddSongDialogFragment : DialogFragment(), SearchView.OnQueryTextListener {

    private var _binding: DialogFragmentAlbumAddSongBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentAlbumAddSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            binding.albumAddSongSearchView.setOnQueryTextListener(this@AlbumAddSongDialogFragment)
            viewModel.songs.observe(viewLifecycleOwner) {
                if (viewModel.getSongsWithoutAnAlbum().isEmpty()) {
                    albumAddSongRvEmpty.visibility = View.VISIBLE
                    albumAddSongSearchView.visibility = View.GONE
                    albumRvSongsWithoutAlbum.visibility = View.GONE
                } else {
                    albumAddSongRvEmpty.visibility = View.GONE
                    albumAddSongSearchView.visibility = View.VISIBLE
                    albumRvSongsWithoutAlbum.visibility = View.VISIBLE
                    albumRvSongsWithoutAlbum.adapter = SongAdapter(
                            this@AlbumAddSongDialogFragment,
                            viewModel.getSongsWithoutAnAlbum().sorted().reversed(),
                            viewModel,
                            null,
                            null,
                            { song -> onClickSong(song) },
                            "",
                            false
                        )
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.albumAddSongSearchView.setQuery("", false)
        viewModel.filterSongs()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onClickSong(song: Song) {
        AndroidUtils.loadDialogFragment(
            viewModel.viewModelScope,
            this@AlbumAddSongDialogFragment
        ) {
            viewModel.addSongToAlbum(viewModel.selectedAlbum.value!!, song)
        }
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.album_add_song_toast)
        )
        super.dismiss()
    }

    companion object {
        const val TAG = Constants.Song.Album.ADD_SONG_TAG
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            this@AlbumAddSongDialogFragment.requireContext(),
            resources.getString(R.string.song_filter_toast)
        )
        binding.albumAddSongSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterSongs(name = newText)
        return false
    }
}