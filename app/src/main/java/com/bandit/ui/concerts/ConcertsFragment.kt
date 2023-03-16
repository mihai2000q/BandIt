package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.helper.TouchHelper
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ConcertsFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
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
            AndroidUtils.setupRefreshLayout(this@ConcertsFragment, concertsRvList)
            concertsSearchView.setOnQueryTextListener(this@ConcertsFragment)
            concertsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    concertAddDialogFragment,
                    childFragmentManager
                )
                concertsBtOptions.performClick()
            }
            concertsBtFilter.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    concertFilterDialogFragment,
                    childFragmentManager
                )
                concertsBtOptions.performClick()
            }
            val touchHelper = TouchHelper<Concert>(
                super.requireContext(),
                concertsRvList,
                { concert -> onDeleteConcert(concert) },
                { concert -> onEditConcert(concert) }
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(concertsRvList)
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.concerts,
                concertsRvList,
                concertsRvEmpty,
                concertsRvBandEmpty,
                bandViewModel.band,
                {
                    touchHelper.updateItems(it.sorted())
                    return@setRecyclerViewEmpty ConcertAdapter(
                        this@ConcertsFragment,
                        it.sorted(),
                        viewModel,
                        { concert -> onDeleteConcert(concert) },
                        { concert -> onEditConcert(concert) }
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
            AndroidUtils.setupFabOptionsWithBand(
                this@ConcertsFragment,
                concertsRvList,
                bandViewModel.band,
                concertsBtOptions,
                concertsBtAdd,
                concertsBtFilter
            )
            AndroidUtils.setupFabScrollUp(
                super.requireContext(),
                concertsRvList,
                concertsBtScrollUp
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onDeleteConcert(concert: Concert) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.concert_alert_dialog_title),
            resources.getString(R.string.concert_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                this@ConcertsFragment) {
                coroutineScope {
                    async {
                        launch { viewModel.removeConcert(concert) }
                        launch {
                            val events = scheduleViewModel.events.value!!.filter {
                                it.id == concert.id && it.name == concert.name
                            }
                            if(events.isNotEmpty())
                                scheduleViewModel.removeEvent(events.first())
                        }
                    }
                }.await()
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.concert_remove_toast),
            )
        }
    }

    private fun onEditConcert(concert: Concert) {
        val concertEditDialogFragment = ConcertEditDialogFragment()
        viewModel.selectedConcert.value = concert
        AndroidUtils.showDialogFragment(
            concertEditDialogFragment,
            childFragmentManager
        )
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