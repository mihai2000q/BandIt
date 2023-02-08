package com.bandit.ui.concerts

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.di.DILocator
import com.bandit.mapper.ConcertMapper
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils
import java.time.Duration

class ConcertAddDialogFragment : ConcertDialogFragment() {

    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    private val _database = DILocator.database

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerType()
        with(binding) {
            concertButton.setText(R.string.bt_add)
            concertButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadTask(this@ConcertAddDialogFragment) { addConcert() }
            }
        }
    }

    private suspend fun addConcert() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                concertButton
            )
            val concert = Concert(
                name = concertEtName.text.toString(),
                dateTime = ParserUtils.parseDateTime(
                    concertEtDate.text.toString(),
                    concertEtTime.text.toString()
                ),
                duration = ParserUtils.parseDuration(concertEtDuration.text.toString()),
                bandId = _database.currentBand.id,
                city = concertEtCity.text.toString(),
                country = concertEtCountry.text.toString(),
                place = concertEtPlace.text.toString(),
                concertType = BandItEnums.Concert.Type.values()[typeIndex],
            )
            viewModel.addConcert(concert)
            scheduleViewModel.addEvent(ConcertMapper.fromConcertToEvent(concert))
            AndroidUtils.toastNotification(
                super.requireContext(),
                resources.getString(R.string.concert_add_toast)
            )
            concertEtName.setText("")
            concertEtDate.setText("")
            concertEtTime.setText("")
            concertEtCity.setText("")
            concertEtCountry.setText("")
            concertEtPlace.setText("")
            concertEtDuration.setText("")
            typeIndex = 0
        }
        super.dismiss()
    }

    companion object {
        const val TAG = Constants.Concert.ADD_TAG
    }
}