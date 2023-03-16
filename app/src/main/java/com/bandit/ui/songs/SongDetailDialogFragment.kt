package com.bandit.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.data.model.Song
import com.bandit.databinding.DialogFragmentSongDetailBinding
import com.bandit.extension.print
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SongDetailDialogFragment : BottomSheetDialogFragment() {
    private var _binding: DialogFragmentSongDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentSongDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        viewModel.selectedSong.observe(viewLifecycleOwner) { assignSongDetails(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun assignSongDetails(song: Song) {
        with(binding) {
            songDetailName.text = song.name
            songDetailReleaseDate.text = song.releaseDate.print()
            AndroidUtils.ifNullHide(songDetailAlbumName, songDetailTvAlbumName, song.albumName)
            songDetailDuration.text = song.duration.print()
        }
    }

    companion object {
        const val TAG = Constants.Song.DETAIL_TAG
    }
}