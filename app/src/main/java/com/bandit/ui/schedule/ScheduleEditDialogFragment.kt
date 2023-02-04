package com.bandit.ui.schedule

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.extension.print
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class ScheduleEditDialogFragment : ScheduleDialogFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            scheduleButton.setText(R.string.bt_edit)
            viewModel.selectedEvent.observe(viewLifecycleOwner) {
                scheduleEtName.setText(it.name)
                scheduleEtDate.setText(it.dateTime.toLocalDate().toString())
                scheduleEtTime.setText(it.dateTime.toLocalTime().toString())
                scheduleEtDuration.setText(it.duration.print())
                scheduleSpinnerType.setSelection(it.type.ordinal)
            }
            scheduleButton.setOnClickListener {
                viewModel.editEvent(
                    Event(
                        scheduleEtName.text.toString(),
                        ParserUtils.parseDateTime(scheduleEtDate.text.toString(), scheduleEtTime.text.toString()),
                        ParserUtils.parseDuration(scheduleEtDuration.text.toString()),
                        BandItEnums.Event.Type.values()[typeIndex],
                        viewModel.selectedEvent.value!!.bandId,
                        viewModel.selectedEvent.value!!.id
                    )
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.event_edit_toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Schedule.ADD_TAG
    }
}