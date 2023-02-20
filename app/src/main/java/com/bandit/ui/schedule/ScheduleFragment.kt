package com.bandit.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.FragmentScheduleBinding
import com.bandit.extension.get2Characters
import com.bandit.ui.adapter.EventAdapter
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils
import java.time.Instant
import java.time.ZoneId

class ScheduleFragment : Fragment(), AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()
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
            AndroidComponents.header(
                super.requireActivity(),
                scheduleHeader.headerBtAccount
            )
            scheduleHeader.headerTvTitle.setText(R.string.title_schedule)
            scheduleEventsView.layoutManager = GridLayoutManager(context, 1)
            AndroidComponents.spinner(
                super.requireContext(),
                scheduleSpinnerMode,
                this@ScheduleFragment,
                BandItEnums.Schedule.ViewType.values()
            )
            scheduleSwitchView.setOnCheckedChangeListener { _, isChecked ->
                viewModel.calendarMode.value = isChecked
            }
            viewModel.calendarMode.observe(viewLifecycleOwner) {
                if(it) calendarMode() else listMode()
            }
            scheduleCalendarView.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calendarMode() {
        with(binding) {
            scheduleHeader.root.visibility = View.GONE
            scheduleCalendarView.visibility = View.VISIBLE
            scheduleSearchView.visibility = View.INVISIBLE
            scheduleSpinnerMode.visibility = View.VISIBLE
            scheduleSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 15 / 32

            if(viewModel.currentDate.value == null)
                viewModel.currentDate.value =
                    Instant.ofEpochMilli(scheduleCalendarView.date)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
            else
                scheduleCalendarView.date = viewModel.currentDate.value!!
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            scheduleCalendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                viewModel.currentDate.value = ParserUtils.parseDate(year.toString() +
                        "-" +
                        month.plus(1).toString().get2Characters() +
                        "-" +
                        dayOfMonth.toString().get2Characters())
            }
            viewModel.currentDate.observe(viewLifecycleOwner) {
                viewModel.filterEvents(date = it)
                scheduleAddDialogFragment.date.value = it.toString()
            }
            viewModel.events.observe(viewLifecycleOwner) {
                if(viewModel.calendarMode.value == false) return@observe
                scheduleEventsView.adapter = EventAdapter(
                    this@ScheduleFragment,
                    it.sorted(),
                    viewModel
                )
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
            scheduleHeader.root.visibility = View.VISIBLE
            scheduleCalendarView.visibility = View.GONE
            scheduleSpinnerMode.visibility = View.GONE
            scheduleSearchView.visibility = View.VISIBLE
            scheduleSearchView.layoutParams.width = AndroidUtils.getScreenWidth(super.requireActivity()) * 3 / 4
            viewModel.removeFilters()
            viewModel.events.observe(viewLifecycleOwner) {
                if(viewModel.calendarMode.value == true) return@observe
                scheduleEventsView.adapter = EventAdapter(
                    this@ScheduleFragment,
                    it.sorted(),
                    viewModel
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
        AndroidComponents.toastNotification(
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