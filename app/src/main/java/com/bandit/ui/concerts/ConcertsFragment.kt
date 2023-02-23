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
import com.bandit.component.AndroidComponents
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class ConcertsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val concertAddDialogFragment = ConcertAddDialogFragment()
        val concertFilterDialogFragment = ConcertFilterDialogFragment()
        with(binding) {
            AndroidComponents.header(
                super.requireActivity(),
                concertsHeader.headerBtAccount
            )
            concertsSearchView.setOnQueryTextListener(this@ConcertsFragment)
            concertsHeader.headerTvTitle.setText(R.string.title_concerts)
            AndroidUtils.disableIfBandNull(
                resources,
                DILocator.getDatabase().currentBand,
                concertsBtAdd
            ) {
                AndroidUtils.showDialogFragment(
                    concertAddDialogFragment,
                    childFragmentManager
                )
            }
            AndroidUtils.disableIfBandNull(
                resources,
                DILocator.getDatabase().currentBand,
                concertsBtFilter
            ) {
                AndroidUtils.showDialogFragment(
                    concertFilterDialogFragment,
                    childFragmentManager
                )
            }
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.concerts,
                concertsList,
                concertsRvEmpty,
                concertsRvBandEmpty,
                {
                    return@setRecyclerViewEmpty ConcertAdapter(
                        this@ConcertsFragment,
                        it.sorted(),
                        viewModel
                    )
                }
            )
            val badgeDrawable = BadgeDrawable.create(super.requireContext())
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
        viewModel.filterConcerts(name = newText)
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