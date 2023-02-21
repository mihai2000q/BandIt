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
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.FragmentScheduleBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.EventAdapter
import com.bandit.util.AndroidUtils
import java.util.*


class ScheduleFragment : Fragment(),
    AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener,
    OnDayClickListener {

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

            with(viewModel.currentDate.value!!) {
                val cal = Calendar.Builder()
                cal.setDate(year, month.ordinal, dayOfMonth)
                scheduleCalendarView.setDate(cal.build())
            }
            highlightDays()
            scheduleCalendarView.setOnDayClickListener(this@ScheduleFragment)
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
            AndroidUtils.disableIfBandNull(
                resources,
                DILocator.getDatabase().currentBand,
                scheduleBtAdd
            ) {
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
            viewModel.removeFilters()
            viewModel.events.observe(viewLifecycleOwner) {
                if(viewModel.calendarMode.value == true) return@observe
                scheduleEventsView.adapter = EventAdapter(
                    this@ScheduleFragment,
                    it.sorted(),
                    viewModel
                )
            }
            AndroidUtils.disableIfBandNull(
                resources,
                DILocator.getDatabase().currentBand,
                scheduleBtAdd
            ) {
                scheduleAddDialogFragment.date.value = null
                AndroidUtils.showDialogFragment(
                    scheduleAddDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    private fun highlightDays() {
        val days = mutableListOf<CalendarDay>()
        viewModel.getDates().forEach {
            val cal = Calendar.Builder()
            cal.setDate(it.year, it.month.ordinal, it.dayOfMonth)
            val day = CalendarDay(cal.build())
            day.labelColor = R.color.white
            day.backgroundResource = R.color.dark_spring_green
            days.add(day)
        }
        binding.scheduleCalendarView.setCalendarDays(days)
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

    override fun onDayClick(eventDay: EventDay) {
        val date = AndroidUtils.fromCalendarToLocalDate(eventDay.calendar)
        viewModel.currentDate.value = date
        viewModel.filterEvents(date = date)
    }
}