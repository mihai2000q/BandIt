package com.bandit.ui.schedule

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Event
import com.bandit.di.DILocator
import com.bandit.mapper.EventMapper
import com.bandit.ui.concerts.ConcertsViewModel
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class ScheduleAddDialogFragment : ScheduleDialogFragment() {

    private val concertViewModel: ConcertsViewModel by activityViewModels()
    val date = MutableLiveData("")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            date.observe(viewLifecycleOwner) { scheduleEtDate.setText(date.value) }
            scheduleButton.setOnClickListener {
                if(validateFields())
                    addEvent()
            }
        }
    }

    private fun addEvent() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                scheduleEtName
            )
            val event = Event(
                name = scheduleEtName.text.toString(),
                dateTime = ParserUtils.parseDateTime(scheduleEtDate.text.toString(),
                    scheduleEtTime.text.toString()),
                duration = ParserUtils.parseDuration(scheduleEtDuration.text.toString()),
                type = BandItEnums.Event.Type.values()[typeIndex],
                bandId = DILocator.database.currentBand.id
            )
            viewModel.addEvent(event)
            if(event.type == BandItEnums.Event.Type.Concert)
                concertViewModel.addConcert(EventMapper.fromEventToConcert(event))
            AndroidUtils.toastNotification(
                super.requireContext(),
                resources.getString(R.string.event_add_toast)
            )
            scheduleEtName.setText("")
            scheduleEtDate.setText("")
            scheduleEtTime.setText("")
            scheduleEtDuration.setText("")
            typeIndex = 0
            super.dismiss()
        }
    }

    companion object {
        const val TAG = Constants.Schedule.ADD_TAG
    }
}