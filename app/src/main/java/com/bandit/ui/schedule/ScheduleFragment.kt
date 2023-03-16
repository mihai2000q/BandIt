package com.bandit.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
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


class ScheduleFragment : Fragment(), SearchView.OnQueryTextListener,
    OnDayClickListener {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ScheduleViewModel by activityViewModels()
    private val concertViewModel: ConcertsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private val scheduleAddDialogFragment = ScheduleAddDialogFragment()
    private val scheduleEditDialogFragment = ScheduleEditDialogFragment()
    private lateinit var touchHelper: TouchHelper<Event>

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
            AndroidUtils.setupRefreshLayout(this@ScheduleFragment, scheduleRvEventsView)
            scheduleRvEventsView.layoutManager = GridLayoutManager(context, 1)
            scheduleSearchView.setOnQueryTextListener(this@ScheduleFragment)
            scheduleBtCalendarMode.setOnClickListener {
                viewModel.calendarMode.value = !viewModel.calendarMode.value!!
                scheduleBtOptions.performClick()
            }
            viewModel.calendarMode.observe(viewLifecycleOwner) {
                if(it) calendarMode() else listMode()
            }
            AndroidUtils.setupFabOptionsWithBand(
                this@ScheduleFragment,
                scheduleRvEventsView,
                bandViewModel.band,
                scheduleBtOptions,
                listOf(scheduleBtAdd, scheduleBtCalendarMode),
                listOf(scheduleFabTvAdd, scheduleFabTvMode)
            )
            AndroidUtils.setupFabScrollUp(
                super.requireContext(),
                scheduleRvEventsView,
                scheduleBtScrollUp
            )
            touchHelper = TouchHelper(
                super.requireContext(),
                scheduleRvEventsView,
                { event -> onDeleteEvent(event) },
                { event -> onEditEvent(event) }
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(scheduleRvEventsView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun calendarMode() {
        with(binding) {
            scheduleTvEmpty.setText(R.string.recycler_view_calendar_empty)
            scheduleBtCalendarMode.contentDescription = resources.getString(R.string.content_description_bt_calendar_view)
            scheduleBtCalendarMode.tooltipText = resources.getString(R.string.content_description_bt_calendar_view)
            scheduleFabTvMode.text = resources.getString(R.string.schedule_fab_calendar_mode)
            scheduleBtCalendarMode.setImageDrawable(
                ContextCompat.getDrawable(
                    super.requireContext(),
                    R.drawable.ic_list
                )
            )
            scheduleCalendarView.visibility = View.VISIBLE
            scheduleSearchView.visibility = View.GONE
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
                    touchHelper.updateItems(it.sorted())
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
            scheduleBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    scheduleAddDialogFragment,
                    childFragmentManager
                )
                scheduleBtOptions.performClick()
            }
        }
    }

    private fun listMode() {
        with(binding) {
            scheduleTvEmpty.setText(R.string.recycler_view_event_empty)
            scheduleBtCalendarMode.contentDescription = resources.getString(R.string.content_description_bt_list_events_view)
            scheduleBtCalendarMode.tooltipText = resources.getString(R.string.content_description_bt_list_events_view)
            scheduleFabTvMode.text = resources.getString(R.string.schedule_fab_list_mode)
            scheduleBtCalendarMode.setImageDrawable(
                ContextCompat.getDrawable(
                    super.requireContext(),
                    R.drawable.ic_schedule
                )
            )
            scheduleCalendarView.visibility = View.GONE
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
                    touchHelper.updateItems(it.sorted())
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
            scheduleBtAdd.setOnClickListener {
                scheduleAddDialogFragment.date.value = null
                AndroidUtils.showDialogFragment(
                    scheduleAddDialogFragment,
                    childFragmentManager
                )
                scheduleBtOptions.performClick()
            }
        }
    }

    private fun highlightDays() {
        val days = mutableListOf<CalendarDay>()
        viewModel.getDates().forEach {
            val cal = Calendar.Builder()
            cal.setDate(it.year, it.month.ordinal, it.dayOfMonth)
            val day = CalendarDay(cal.build())
            day.labelColor = R.color.calendar_day_label
            day.backgroundResource = R.color.calendar_day_background
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