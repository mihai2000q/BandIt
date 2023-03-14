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
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Album
import com.bandit.data.model.Song
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.helper.TouchHelper
import com.bandit.ui.songs.albums.AlbumAddDialogFragment
import com.bandit.ui.songs.albums.AlbumDetailDialogFragment
import com.bandit.ui.songs.albums.AlbumEditDialogFragment
import com.bandit.ui.songs.albums.AlbumFilterDialogFragment
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class SongsFragment : Fragment() {

    private var _binding: FragmentSongsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SongsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private lateinit var badgeDrawable: BadgeDrawable
    private val songEditDialogFragment = SongEditDialogFragment()
    private val albumEditDialogFragment = AlbumEditDialogFragment()
    private val albumDetailDialogFragment = AlbumDetailDialogFragment ({ onEditSong(it) }) { onDeleteAlbum(it) }

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
            AndroidUtils.setupRefreshLayout(this@SongsFragment, songsRvList)
            AndroidUtils.setupRefreshLayout(this@SongsFragment, songsRvAlbums)
            badgeDrawable = BadgeDrawable.create(super.requireContext())
            AndroidUtils.disableIfBandEmpty(
                viewLifecycleOwner,
                resources,
                bandViewModel.band,
                songsBtAlbumMode
            ) {
                songsSearchView.setQuery("", false)
                viewModel.albumMode.value = !viewModel.albumMode.value!!
                songsBtOptions.performClick()
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
            songsRvAlbums.visibility = View.VISIBLE
            songsBtAlbumMode.tooltipText = resources.getString(R.string.content_description_bt_songs_view)
            songsTvRvEmpty.setText(R.string.recycler_view_album_empty)
            mode(
                R.drawable.ic_list,
                albumAddDialogFragment,
                albumFilterDialogFragment
            )
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.albums,
                songsRvAlbums,
                songsRvEmpty,
                songsRvBandEmpty,
                bandViewModel.band,
                {
                    return@setRecyclerViewEmpty AlbumAdapter(
                        this@SongsFragment,
                        it.sorted().reversed(),
                        viewModel,
                        { album -> onDeleteAlbum(album) },
                        { album -> onEditAlbum(album) },
                        { album -> onClickAlbum(album) }
                    )
                }
            ) {
                songsRvList.visibility = View.GONE
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
            AndroidUtils.setupFabOptionsCheckBand(
                this@SongsFragment,
                songsRvAlbums,
                bandViewModel.band,
                songsBtOptions,
                songsBtAdd,
                songsBtFilter,
                songsBtAlbumMode
            )
        }
    }

    private fun songMode() {
        val songAddDialogFragment = SongAddDialogFragment()
        val songFilterDialogFragment = SongFilterDialogFragment(badgeDrawable)
        with(binding) {
            songsRvList.visibility = View.VISIBLE
            songsBtAlbumMode.tooltipText = resources.getString(R.string.content_description_bt_album_view)
            songsTvRvEmpty.setText(R.string.recycler_view_songs_empty)
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
                    ItemTouchHelper(object: TouchHelper<Song>(
                        super.requireContext(),
                        songsRvList,
                        { song -> onDeleteSong(song) },
                        { song -> onEditSong(song) }
                    ) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            items = it.sorted().reversed()
                            super.onSwiped(viewHolder, direction)
                        }
                    }).attachToRecyclerView(songsRvList)
                    return@setRecyclerViewEmpty SongAdapter(
                        this@SongsFragment,
                        it.sorted().reversed(),
                        viewModel,
                        { song -> onDeleteSong(song) },
                        { song -> onEditSong(song) }
                    )
                }
            ) {
                songsRvAlbums.visibility = View.GONE
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
            AndroidUtils.setupFabOptionsCheckBand(
                this@SongsFragment,
                songsRvList,
                bandViewModel.band,
                songsBtOptions,
                songsBtAdd,
                songsBtFilter,
                songsBtAlbumMode
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
                songsBtOptions.performClick()
           }
            songsBtFilter.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    filterDialogFragment,
                    childFragmentManager
                )
                songsBtOptions.performClick()
            }
        }
    }

    private fun onDeleteSong(song: Song) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.song_alert_dialog_title),
            resources.getString(R.string.song_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this@SongsFragment) {
                viewModel.removeSong(song)
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.song_remove_toast),
            )
        }
    }

    private fun onEditSong(song: Song) {
        viewModel.selectedSong.value = song
        AndroidUtils.showDialogFragment(
            songEditDialogFragment,
            childFragmentManager
        )
    }

    private fun onDeleteAlbum(album: Album) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.album_alert_dialog_title),
            resources.getString(R.string.album_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
                viewModel.removeAlbum(album)
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.album_remove_toast),
            )
        }
    }

    private fun onEditAlbum(album: Album) {
        viewModel.selectedAlbum.value = album
        AndroidUtils.showDialogFragment(
            albumEditDialogFragment,
            childFragmentManager
        )
    }

    private fun onClickAlbum(album: Album) {
        viewModel.selectedAlbum.value = album
        AndroidUtils.showDialogFragment(
            albumDetailDialogFragment,
            childFragmentManager
        )
    }
}