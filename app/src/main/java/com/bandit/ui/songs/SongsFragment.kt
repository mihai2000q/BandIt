package com.bandit.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.songs.albums.AlbumAddDialogFragment
import com.bandit.ui.songs.albums.AlbumFilterDialogFragment
import com.bandit.util.AndroidUtils

class SongsFragment : Fragment() {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()

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
                songsHeader.headerBtAccount
            )
            songsHeader.headerTvTitle.setText(R.string.title_songs)
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
                R.drawable.ic_list,
                albumAddDialogFragment,
                albumFilterDialogFragment
            )
            viewModel.albums.observe(viewLifecycleOwner) {
                if(viewModel.albumMode.value == false) return@observe
                songsList.adapter = AlbumAdapter(this@SongsFragment, it, viewModel)
            }
            songsSearchView.setOnQueryTextListener(
                object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        AndroidComponents.toastNotification(
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
                R.drawable.ic_album_view,
                songAddDialogFragment,
                songFilterDialogFragment
            )
            viewModel.songs.observe(viewLifecycleOwner) {
                if(viewModel.albumMode.value == true) return@observe
                songsList.adapter = SongAdapter(
                    this@SongsFragment,
                    it.sorted().reversed(),
                    viewModel,
                    { song ->
                        AndroidComponents.alertDialog(
                            super.requireContext(),
                            resources.getString(R.string.song_alert_dialog_title),
                            resources.getString(R.string.song_alert_dialog_message),
                            resources.getString(R.string.alert_dialog_positive),
                            resources.getString(R.string.alert_dialog_negative)
                        ) {
                            AndroidUtils.loadTask(this@SongsFragment) {
                                viewModel.removeSong(song)
                            }
                            AndroidComponents.toastNotification(
                                super.requireContext(),
                                resources.getString(R.string.song_remove_toast),
                            )
                        }
                        return@SongAdapter true
                    }
                )
            }
            songsSearchView.setOnQueryTextListener(
                object : OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        AndroidComponents.toastNotification(
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