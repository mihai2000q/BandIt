package com.bandit.ui.songs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.builder.AndroidComponents
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.songs.albums.AlbumDialogFragment
import com.bandit.util.AndroidUtils

class SongsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidComponents.header(
                super.requireActivity(),
                header.headerBtAccount,
                header.headerBtBand,
                viewLifecycleOwner,
                bandViewModel.band
            )
            header.headerTvTitle.setText(R.string.title_songs)
            songsSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 5 / 8
            songsSearchView.setOnQueryTextListener(this@SongsFragment)
            songsBtAlbumMode.setOnClickListener { viewModel.albumMode.value = !viewModel.albumMode.value!! }
            viewModel.albumMode.observe(viewLifecycleOwner) {
                if(it) albumMode() else songMode()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun albumMode() {
        val albumAddDialogFragment = AlbumDialogFragment()
        val albumFilterDialogFragment = AlbumDialogFragment()
        with(binding) {
            mode(
                R.drawable.ic_baseline_list,
                albumAddDialogFragment,
                albumFilterDialogFragment
            )
            viewModel.albums.observe(viewLifecycleOwner) {
                songsList.adapter = AlbumAdapter(it)
            }
        }
    }

    private fun songMode() {
        val songAddDialogFragment = SongAddDialogFragment()
        val songFilterDialogFragment = SongFilterDialogFragment()
        with(binding) {
            mode(
                R.drawable.ic_baseline_album_view,
                songAddDialogFragment,
                songFilterDialogFragment
            )
            viewModel.songs.observe(viewLifecycleOwner) {
                songsList.adapter = SongAdapter(
                    it.sorted().reversed(),
                    viewModel,
                    childFragmentManager
                )
            }
        }
    }

    private fun mode(
        drawableIcon: Int,
        addDialogFragment: DialogFragment,
        filterDialogFragment: DialogFragment
    ) {
        with(binding) {
            songsBtAlbumMode.setImageDrawable(
                ContextCompat.getDrawable(
                    super.requireContext(),
                    drawableIcon
                )
            )
            songsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    addDialogFragment,
                    childFragmentManager
                )
            }
            songsBtFilter.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    filterDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.song_filter_toast)
        )
        binding.songsSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterSongs(name = newText)
        return false
    }
}