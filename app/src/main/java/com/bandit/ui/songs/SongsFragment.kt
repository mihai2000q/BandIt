package com.bandit.ui.songs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bandit.R
import com.bandit.builder.AndroidComponents
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.songs.albums.AlbumAddDialogFragment
import com.bandit.ui.songs.albums.AlbumFilterDialogFragment
import com.bandit.util.AndroidUtils

class SongsFragment : Fragment() {

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
                songsHeader.headerBtAccount,
                songsHeader.headerBtBand,
                viewLifecycleOwner,
                bandViewModel.band
            )
            songsHeader.headerTvTitle.setText(R.string.title_songs)
            songsSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 5 / 8
            songsBtAlbumMode.setOnClickListener {
                songsSearchView.setQuery("", false)
                viewModel.albumMode.value = !viewModel.albumMode.value!!
            }
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
        val albumAddDialogFragment = AlbumAddDialogFragment()
        val albumFilterDialogFragment = AlbumFilterDialogFragment()
        with(binding) {
            songsList.layoutManager = GridLayoutManager(context, 2)
            mode(
                R.drawable.ic_baseline_list,
                albumAddDialogFragment,
                albumFilterDialogFragment
            )
            viewModel.albums.observe(viewLifecycleOwner) {
                if(viewModel.albumMode.value == false) return@observe
                songsList.adapter = AlbumAdapter(super.requireActivity(), it, viewModel, childFragmentManager)
            }
            songsSearchView.setOnQueryTextListener(
                object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        AndroidUtils.toastNotification(
                            this@SongsFragment.requireContext(),
                            resources.getString(R.string.album_filter_toast)
                        )
                        binding.songsSearchView.clearFocus()
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        viewModel.filterAlbums(name = newText)
                        return false
                    }
                }
            )
        }
    }

    private fun songMode() {
        val songAddDialogFragment = SongAddDialogFragment()
        val songFilterDialogFragment = SongFilterDialogFragment()
        with(binding) {
            songsList.layoutManager = GridLayoutManager(context, 1)
            mode(
                R.drawable.ic_baseline_album_view,
                songAddDialogFragment,
                songFilterDialogFragment
            )
            viewModel.songs.observe(viewLifecycleOwner) {
                if(viewModel.albumMode.value == true) return@observe
                songsList.adapter = SongAdapter(
                    it.sorted().reversed(),
                    viewModel,
                    childFragmentManager,
                    { song ->
                        AndroidUtils.toastNotification(
                            super.requireContext(),
                            resources.getString(R.string.song_remove_toast),
                        )
                        return@SongAdapter viewModel.removeSong(song)
                    }
                )
            }
            songsSearchView.setOnQueryTextListener(
                object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        AndroidUtils.toastNotification(
                            this@SongsFragment.requireContext(),
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
            )
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
}