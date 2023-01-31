package com.bandit.ui.songs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.databinding.FragmentSongsBinding
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.adapter.SongAdapter
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.band.CreateBandDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.Header

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
        val songAddDialogFragment = SongAddDialogFragment()
        val songDetailDialogFragment = SongDetailDialogFragment()
        val songEditDialogFragment = SongEditDialogFragment()
        val songFilterDialogFragment = SongFilterDialogFragment()
        with(binding) {
            Header(
                super.requireActivity(),
                header.headerBtAccount,
                header.headerBtBand,
                viewLifecycleOwner,
                bandViewModel.band
            )
            header.headerTvTitle.setText(R.string.title_songs)
            songsSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 11 / 15
            songsSearchView.setOnQueryTextListener(this@SongsFragment)
            songsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    songAddDialogFragment,
                    childFragmentManager
                )
            }
            songsBtFilter.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    songFilterDialogFragment,
                    childFragmentManager
                )
            }
            with(viewModel) {
                songs.observe(viewLifecycleOwner) {
                    songsList.adapter = SongAdapter(
                        it,
                        { song ->
                            selectedSong.value = song
                            AndroidUtils.showDialogFragment(
                                songDetailDialogFragment,
                                childFragmentManager
                            )
                        },
                        { song -> selectedSong.value = song; return@SongAdapter true },
                        { song ->
                            AndroidUtils.toastNotification(
                                super.requireContext(),
                                resources.getString(R.string.song_remove_toast),
                            )
                            return@SongAdapter removeSong(song)
                        }
                    ) { concert ->
                        selectedSong.value = concert
                        AndroidUtils.showDialogFragment(
                            songEditDialogFragment,
                            childFragmentManager
                        )
                        return@SongAdapter true
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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