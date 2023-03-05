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
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.Song
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.songs.albums.AlbumAddDialogFragment
import com.bandit.ui.songs.albums.AlbumFilterDialogFragment
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class SongsFragment : Fragment() {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private lateinit var badgeDrawable: BadgeDrawable

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
            badgeDrawable = BadgeDrawable.create(super.requireContext())
            bandViewModel.band.observe(viewLifecycleOwner) {
                AndroidUtils.disableIfBandNull(
                    resources,
                    it,
                    songsBtAlbumMode
                ) {
                    songsSearchView.setQuery("", false)
                    viewModel.albumMode.value = !viewModel.albumMode.value!!
                }
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
        val albumFilterDialogFragment = AlbumFilterDialogFragment(badgeDrawable)
        with(binding) {
            songsTvRvEmpty.setText(R.string.recycler_view_album_empty)
            songsRvList.layoutManager = GridLayoutManager(context, 2)
            mode(
                R.drawable.ic_list,
                albumAddDialogFragment,
                albumFilterDialogFragment
            )
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.albums,
                songsRvList,
                songsRvEmpty,
                songsRvBandEmpty,
                bandViewModel.band,
                {
                    return@setRecyclerViewEmpty AlbumAdapter(
                        this@SongsFragment, it, viewModel)
                }
            ) {
                if(viewModel.albumMode.value == false) return@setRecyclerViewEmpty
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
                        viewModel.albumFilters.value?.set(
                            SongsViewModel.AlbumFilter.Name, newText ?: "")
                        return false
                    }
                }
            )
            badgeDrawable.isVisible = viewModel.getAlbumFiltersOn() > 0
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                songsBtFilter,
                viewModel.getAlbumFiltersOn(),
                viewModel.getAlbumFiltersOn() > 0,
                ContextCompat.getColor(super.requireContext(), R.color.blue)
            )
            // for when I change modes between album and song
            badgeDrawable.number = viewModel.getAlbumFiltersOn()
            badgeDrawable.isVisible = viewModel.getAlbumFiltersOn() > 0
            // for when I filter
            viewModel.albumFilters.observe(viewLifecycleOwner) {
                badgeDrawable.number = viewModel.getAlbumFiltersOn()
                badgeDrawable.isVisible = viewModel.getAlbumFiltersOn() > 0
            }
        }
    }

    private fun songMode() {
        val songAddDialogFragment = SongAddDialogFragment()
        val songFilterDialogFragment = SongFilterDialogFragment(badgeDrawable)
        with(binding) {
            songsTvRvEmpty.setText(R.string.recycler_view_songs_empty)
            songsRvList.layoutManager = GridLayoutManager(context, 1)
            mode(
                R.drawable.ic_album_view,
                songAddDialogFragment,
                songFilterDialogFragment
            )
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.songs,
                songsRvList,
                songsRvEmpty,
                songsRvBandEmpty,
                bandViewModel.band,
                {
                    return@setRecyclerViewEmpty SongAdapter(
                        this@SongsFragment,
                        it.sorted().reversed(),
                        viewModel,
                        { song -> onDeleteSong(song) }
                    )
                }
            ) {
                if(viewModel.albumMode.value == true) return@setRecyclerViewEmpty
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
                        viewModel.songFilters.value?.set(
                            SongsViewModel.SongFilter.Name, newText ?: "")
                        return false
                    }
                }
            )
            badgeDrawable.number = viewModel.getSongFiltersOn()
            badgeDrawable.isVisible = viewModel.getSongFiltersOn() > 0
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                songsBtFilter,
                viewModel.getSongFiltersOn(),
                viewModel.getSongFiltersOn() > 0,
                ContextCompat.getColor(super.requireContext(), R.color.blue)
            )
            viewModel.songFilters.observe(viewLifecycleOwner) {
                badgeDrawable.number = viewModel.getSongFiltersOn()
                badgeDrawable.isVisible = viewModel.getSongFiltersOn() > 0
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
            bandViewModel.band.observe(viewLifecycleOwner) {
                AndroidUtils.disableIfBandNull(
                    resources,
                    it,
                    songsBtAdd
                ) {
                    AndroidUtils.showDialogFragment(
                        addDialogFragment,
                        childFragmentManager
                    )
                }
                AndroidUtils.disableIfBandNull(
                    resources,
                    it,
                    songsBtFilter
                ) {
                    AndroidUtils.showDialogFragment(
                        filterDialogFragment,
                        childFragmentManager
                    )
                }
            }
        }
    }

    private fun onDeleteSong(song: Song): Boolean {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.song_alert_dialog_title),
            resources.getString(R.string.song_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(this@SongsFragment) {
                viewModel.removeSong(song)
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.song_remove_toast),
            )
        }
        return true
    }
}