package com.bandit.ui.songs.albums

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentAlbumAddSongBinding
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

class AlbumAddSongDialogFragment : DialogFragment() {

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
            albumRvSongsWithoutAlbum.adapter =
                SongAdapter(
                    this@AlbumAddSongDialogFragment,
                    viewModel.getSongsWithoutAnAlbum().sorted().reversed(),
                    viewModel,
                    { return@SongAdapter true },
                    resources.getString(R.string.album_remove_from_album),
                    false
                ) {
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@AlbumAddSongDialogFragment) {
                        viewModel.addSongToAlbum(viewModel.selectedAlbum.value!!, it)
                    }
                    AndroidComponents.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.album_add_song_toast)
                    )
                    super.dismiss()
                    return@SongAdapter
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Song.Album.ADD_SONG_TAG
    }
}