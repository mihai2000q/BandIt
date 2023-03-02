package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class ConcertsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val concertAddDialogFragment = ConcertAddDialogFragment()
        val badgeDrawable = BadgeDrawable.create(super.requireContext())
        val concertFilterDialogFragment = ConcertFilterDialogFragment(badgeDrawable)
        with(binding) {
            concertsSearchView.setOnQueryTextListener(this@ConcertsFragment)
            bandViewModel.band.observe(viewLifecycleOwner) {
                AndroidUtils.disableIfBandNull(
                    resources,
                    it,
                    concertsBtAdd
                ) {
                    AndroidUtils.showDialogFragment(
                        concertAddDialogFragment,
                        childFragmentManager
                    )
                }
                AndroidUtils.disableIfBandNull(
                    resources,
                    it,
                    concertsBtFilter
                ) {
                    AndroidUtils.showDialogFragment(
                        concertFilterDialogFragment,
                        childFragmentManager
                    )
                }
            }
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.concerts,
                concertsList,
                concertsRvEmpty,
                concertsRvBandEmpty,
                bandViewModel.band,
                {
                    return@setRecyclerViewEmpty ConcertAdapter(
                        this@ConcertsFragment,
                        it.sorted(),
                        viewModel
                    )
                }
            )
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                concertsBtFilter,
                viewModel.getFiltersOn(),
                viewModel.getFiltersOn() > 0,
                ContextCompat.getColor(super.requireContext(), R.color.blue)
            )
            viewModel.filters.observe(viewLifecycleOwner) {
                badgeDrawable.number = viewModel.getFiltersOn()
                badgeDrawable.isVisible = viewModel.getFiltersOn() > 0
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if(binding.concertsSearchView.width > 0) {
            viewModel.filterConcerts(name = newText)
            viewModel.filters.value?.put(ConcertsViewModel.Filter.Name, newText ?: "")
        }
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.concert_filter_toast)
        )
        binding.concertsSearchView.clearFocus()
        return false
    }
}