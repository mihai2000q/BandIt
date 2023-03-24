package com.bandit.ui.schedule

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.mapper.EventMapper
import com.bandit.data.model.Event
import com.bandit.extension.printHoursAndMinutes
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.ui.template.ScheduleDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ScheduleEditDialogFragment : ScheduleDialogFragment() {
    private val concertViewModel: ConcertsViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            scheduleButton.setText(R.string.bt_edit)
            viewModel.selectedEvent.observe(viewLifecycleOwner) {
                scheduleEtName.setText(it.name)
                scheduleEtDate.setText(it.dateTime.toLocalDate().toString())
                scheduleEtTime.setText(it.dateTime.toLocalTime().toString())
                scheduleEtDuration.setText(it.duration.printHoursAndMinutes())
                scheduleSpinnerType.setSelection(it.type.ordinal)
            }
            scheduleButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@ScheduleEditDialogFragment) { editEvent() }
            }
        }
    }

    override fun validateFields(): Boolean {
        val result = super.validateFields()
        with(binding) {
            with(viewModel.selectedEvent.value!!) {
                if (scheduleEtName.text.toString() == name &&
                    scheduleEtDate.text.toString() == dateTime.toLocalDate().toString() &&
                    scheduleEtTime.text.toString() == dateTime.toLocalTime().toString() &&
                    scheduleEtDuration.text.toString() == duration.printHoursAndMinutes() &&
                    BandItEnums.Event.Type.values()[typeIndex] == type
                ) {
                    scheduleEtName.error = resources.getString(R.string.nothing_changed_validation)
                    return false
                }
            }
        }
        return result
    }

    private suspend fun editEvent() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                scheduleButton
            )
            val newEvent = Event(
                scheduleEtName.text.toString(),
                ParserUtils.parseDateTime(scheduleEtDate.text.toString(), scheduleEtTime.text.toString()),
                ParserUtils.parseDurationTextToHoursAndMinutes(scheduleEtDuration.text.toString()),
                BandItEnums.Event.Type.values()[typeIndex],
                viewModel.selectedEvent.value!!.bandId,
                viewModel.selectedEvent.value!!.id
            )
            coroutineScope {
                async {
                    launch { viewModel.editEvent(newEvent) }
                    if(BandItEnums.Event.Type.values()[typeIndex] == BandItEnums.Event.Type.Concert &&
                        viewModel.selectedEvent.value!!.type == BandItEnums.Event.Type.Concert
                    ) {
                        val concerts = concertViewModel.concerts.value!!
                            .filter { it.id == viewModel.selectedEvent.value!!.id }
                        if(concerts.isNotEmpty())
                            launch {
                                concertViewModel.editConcert(
                                    EventMapper.editEventToConcert(newEvent, concerts.first())
                                )
                            }
                    }
                    else if(BandItEnums.Event.Type.values()[typeIndex] == BandItEnums.Event.Type.Concert) {
                        launch {
                            concertViewModel.addConcert(EventMapper.fromEventToConcert(newEvent))
                        }
                    }
                    return@async
                }
            }.await()

            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.event_edit_toast)
            )
            super.dismiss()
        }
    }

    companion object {
        const val TAG = Constants.Schedule.EDIT_TAG
    }
}