package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.mapper.ConcertMapper
import com.bandit.data.model.Concert
import com.bandit.extension.printHoursAndMinutes
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.ui.template.ConcertDialogFragment
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ConcertEditDialogFragment : ConcertDialogFragment() {
    private val scheduleViewModel: ScheduleViewModel by activityViewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerType()
        with(binding) {
            concertButton.setText(R.string.bt_save)
            viewModel.selectedConcert.observe(viewLifecycleOwner) {
                concertEtName.setText(it.name)
                concertEtCity.setText(it.city)
                concertEtDate.setText(it.dateTime.toLocalDate().toString())
                concertEtTime.setText(it.dateTime.toLocalTime().toString())
                concertEtCountry.setText(it.country)
                concertEtPlace.setText(it.place)
                concertEtDuration.setText(it.duration.printHoursAndMinutes())
                concertEtSpinnerType.setSelection(it.concertType.ordinal)
            }

            concertButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                        this@ConcertEditDialogFragment) { editConcert() }
            }
        }

    }

    override fun validateFields(): Boolean {
        val result = super.validateFields()
        with(binding) {
            with(viewModel.selectedConcert.value!!) {
                if (concertEtName.text.toString() == name &&
                    concertEtDate.text.toString() == dateTime.toLocalDate().toString() &&
                    concertEtTime.text.toString() == dateTime.toLocalTime().toString() &&
                    concertEtCity.text.toString() == city &&
                    concertEtCountry.text.toString() == country &&
                    concertEtPlace.text.toString() == place &&
                    concertEtDuration.text.toString() == duration.printHoursAndMinutes() &&
                    BandItEnums.Concert.Type.values()[typeIndex] == concertType
                ) {
                    concertEtName.error = resources.getString(R.string.nothing_changed_validation)
                    return false
                }
            }
        }
        return result
    }

    private suspend fun editConcert() {
        with(binding) {
            val newConcert = Concert(
                name = concertEtName.text.toString(),
                dateTime = ParserUtils.parseDateTime(concertEtDate.text.toString(),
                    concertEtTime.text.toString()),
                duration = ParserUtils.parseDurationTextToHoursAndMinutes(concertEtDuration.text.toString()),
                bandId = viewModel.selectedConcert.value!!.bandId,
                city = concertEtCity.text.toString(),
                country = concertEtCountry.text.toString(),
                place = concertEtPlace.text.toString(),
                concertType = BandItEnums.Concert.Type.values()[typeIndex],
                id = viewModel.selectedConcert.value!!.id
            )
            coroutineScope {
                async {
                    launch { viewModel.editConcert(newConcert) }
                    launch {
                        val events = scheduleViewModel.events.value!!.filter {
                            it.id == newConcert.id
                        }
                        if(events.isEmpty()) return@launch
                        scheduleViewModel.editEvent(ConcertMapper.fromConcertToEvent(newConcert))
                    }
                }
            }.await()
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.concert_edit_toast)
            )
            super.dismiss()
        }

    }

    companion object {
        const val TAG = Constants.Concert.EDIT_TAG
    }
}