package com.bandit.ui.schedule

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bandit.R
import com.bandit.builder.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.FragmentScheduleBinding
import com.bandit.extension.get2Characters
import com.bandit.ui.adapter.EventAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils
import java.time.Instant
import java.time.ZoneId

class ScheduleFragment : Fragment(), AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private var viewTypeIndex = MutableLiveData(0)
    private val scheduleAddDialogFragment = ScheduleAddDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            scheduleEventsView.layoutManager = GridLayoutManager(context, 1)
            AndroidComponents.header(
                super.requireActivity(),
                scheduleHeader.headerBtAccount,
                scheduleHeader.headerBtBand,
                viewLifecycleOwner,
                bandViewModel.band
            )
            AndroidComponents.spinner(
                super.requireContext(),
                scheduleSpinnerMode,
                this@ScheduleFragment,
                BandItEnums.Schedule.ViewType.values()
            )
            scheduleCalendarMode.setOnClickListener {
                scheduleSearchView.setQuery("", false)
                viewModel.calendarMode.value = !viewModel.calendarMode.value!!
            }
            viewModel.calendarMode.observe(viewLifecycleOwner) {
                if(it) calendarMode() else listMode()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calendarMode() {
        with(binding) {
            scheduleCalendarView.visibility = View.VISIBLE
            scheduleSearchView.visibility = View.INVISIBLE
            scheduleSpinnerMode.visibility = View.VISIBLE
            scheduleSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 15 / 32
            scheduleCalendarMode.setImageDrawable(
                ContextCompat.getDrawable(
                    super.requireContext(),
                    R.drawable.ic_baseline_list
                )
            )
            scheduleAddDialogFragment.date.value =
                Instant.ofEpochMilli(scheduleCalendarView.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .toString()
            scheduleCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                scheduleAddDialogFragment.date.value = year.toString() +
                        "-" +
                        month.toString().get2Characters() +
                        "-" +
                        dayOfMonth.toString().get2Characters()
            }
            scheduleBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    scheduleAddDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    private fun listMode() {
        with(binding) {
            scheduleCalendarView.visibility = View.GONE
            scheduleSpinnerMode.visibility = View.GONE
            scheduleSearchView.visibility = View.VISIBLE
            scheduleSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 3 / 4
            scheduleCalendarMode.setImageDrawable(
                ContextCompat.getDrawable(
                    super.requireContext(),
                    R.drawable.ic_baseline_calendar
                )
            )
            viewModel.events.observe(viewLifecycleOwner) {
                if(viewModel.calendarMode.value == true) return@observe
                scheduleEventsView.adapter = EventAdapter(
                    it.sorted(),
                    viewModel,
                    childFragmentManager
                )
            }
            scheduleBtAdd.setOnClickListener {
                scheduleAddDialogFragment.date.value = null
                AndroidUtils.showDialogFragment(
                    scheduleAddDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewTypeIndex.value = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidUtils.toastNotification(
            this@ScheduleFragment.requireContext(),
            resources.getString(R.string.event_filter_toast)
        )
        binding.scheduleSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterEvents(newText)
        return true
    }
}