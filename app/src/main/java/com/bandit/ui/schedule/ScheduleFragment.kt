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
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnDayClickListener
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.data.model.Event
import com.bandit.databinding.FragmentScheduleBinding
import com.bandit.ui.adapter.EventAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.ui.helper.TouchHelper
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*


class ScheduleFragment : Fragment(),
    AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener,
    OnDayClickListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()
    private val concertViewModel: ConcertsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private var viewTypeIndex = MutableLiveData(0)
    private val scheduleAddDialogFragment = ScheduleAddDialogFragment()
    private val scheduleEditDialogFragment = ScheduleEditDialogFragment()

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
            scheduleRvEventsView.layoutManager = GridLayoutManager(context, 1)
            AndroidComponents.spinner(
                super.requireContext(),
                scheduleSpinnerMode,
                this@ScheduleFragment,
                BandItEnums.Schedule.ViewType.values()
            )
            scheduleSearchView.setOnQueryTextListener(this@ScheduleFragment)
            scheduleSwitchView.setOnCheckedChangeListener { _, isChecked ->
                viewModel.calendarMode.value = isChecked
            }
            viewModel.calendarMode.observe(viewLifecycleOwner) {
                AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                    this@ScheduleFragment) {
                    if(it) calendarMode() else listMode()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calendarMode() {
        with(binding) {
            scheduleTvEmpty.setText(R.string.recycler_view_calendar_empty)
            scheduleCalendarView.visibility = View.VISIBLE
            scheduleSearchView.visibility = View.INVISIBLE
            scheduleSpinnerMode.visibility = View.VISIBLE

            with(viewModel.currentDate.value!!) {
                val cal = Calendar.Builder()
                cal.setDate(this.year, this.month.ordinal, this.dayOfMonth)
                scheduleCalendarView.setDate(cal.build())
            }
            this@ScheduleFragment.highlightDays()
            scheduleCalendarView.setOnDayClickListener(this@ScheduleFragment)
            viewModel.currentDate.observe(viewLifecycleOwner) {
                viewModel.filterEvents(date = it)
                scheduleAddDialogFragment.date.value = it.toString()
            }
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.events,
                scheduleRvEventsView,
                scheduleRvEmpty,
                scheduleRvBandEmpty,
                bandViewModel.band,
                {
                    ItemTouchHelper(object : TouchHelper<Event>(
                        super.requireContext(),
                        scheduleRvEventsView,
                        { event -> onDeleteEvent(event) },
                        { event -> onEditEvent(event) }
                    ) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            items = it.sorted()
                            super.onSwiped(viewHolder, direction)
                        }
                    }).attachToRecyclerView(scheduleRvEventsView)
                    return@setRecyclerViewEmpty EventAdapter(
                        this@ScheduleFragment,
                        it.sorted(),
                        viewModel,
                        { event -> onDeleteEvent(event) },
                        { event -> onEditEvent(event) }
                    )
                }
            ) {
                if(viewModel.calendarMode.value == false) return@setRecyclerViewEmpty
                this@ScheduleFragment.highlightDays()
            }
            AndroidUtils.disableIfBandEmpty(
                viewLifecycleOwner,
                resources,
                bandViewModel.band,
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
            scheduleTvEmpty.setText(R.string.recycler_view_event_empty)
            scheduleCalendarView.visibility = View.GONE
            scheduleSpinnerMode.visibility = View.GONE
            scheduleSearchView.visibility = View.VISIBLE
            viewModel.removeFilters()
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.events,
                scheduleRvEventsView,
                scheduleRvEmpty,
                scheduleRvBandEmpty,
                bandViewModel.band,
                {
                    ItemTouchHelper(object : TouchHelper<Event>(
                        super.requireContext(),
                        scheduleRvEventsView,
                        { event -> onDeleteEvent(event) },
                        { event -> onEditEvent(event) }
                    ) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            items = it.sorted()
                            super.onSwiped(viewHolder, direction)
                        }
                    }).attachToRecyclerView(scheduleRvEventsView)
                    return@setRecyclerViewEmpty EventAdapter(
                        this@ScheduleFragment,
                        it.sorted(),
                        viewModel,
                        { event -> onDeleteEvent(event) },
                        { event -> onEditEvent(event) }
                    )
                }
            ) {
                if(viewModel.calendarMode.value == true) return@setRecyclerViewEmpty
            }
            AndroidUtils.disableIfBandEmpty(
                viewLifecycleOwner,
                resources,
                bandViewModel.band,
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

    private fun onDeleteEvent(event: Event) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.event_alert_dialog_title),
            resources.getString(R.string.event_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                this) {
                coroutineScope {
                    async {
                        launch { viewModel.removeEvent(event) }
                        if(event.type == BandItEnums.Event.Type.Concert)
                            launch {
                                val concerts = concertViewModel.concerts.value!!.filter {
                                    it.id == event.id && it.name == event.name
                                }
                                if(concerts.isNotEmpty())
                                    concertViewModel.removeConcert(concerts.first())
                            }
                    }
                }.await()
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.event_remove_toast)
            )
        }
    }

    private fun onEditEvent(event: Event) {
        viewModel.selectedEvent.value = event
        AndroidUtils.showDialogFragment(
            scheduleEditDialogFragment,
            childFragmentManager
        )
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
        viewModel.filterName.value = newText
        return true
    }

    override fun onDayClick(eventDay: EventDay) {
        val date = AndroidUtils.fromCalendarToLocalDate(eventDay.calendar)
        viewModel.currentDate.value = date
        viewModel.filterEvents(date = date)
    }
}