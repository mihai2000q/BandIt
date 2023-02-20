package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.extension.print
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils

class ConcertEditDialogFragment : ConcertDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerType()
        with(binding) {
            concertButton.setText(R.string.bt_save)
            with(viewModel.selectedConcert.value!!) {
                concertEtName.setText(name)
                concertEtCity.setText(city)
                concertEtDate.setText(dateTime.toLocalDate().toString())
                concertEtTime.setText(dateTime.toLocalTime().toString())
                concertEtCountry.setText(country)
                concertEtPlace.setText(place)
                concertEtDuration.setText(duration.print())
                concertEtSpinnerType.setSelection(concertType?.ordinal ?: 0)
            }

            concertButton.setOnClickListener {
                if(validateFields())
                    AndroidUtils.loadIntent(this@ConcertEditDialogFragment) { editConcert() }
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
                    concertEtDuration.text.toString() == duration.print() &&
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
            viewModel.editConcert(
                Concert(
                    name = concertEtName.text.toString(),
                    dateTime = ParserUtils.parseDateTime(concertEtDate.text.toString(),
                        concertEtTime.text.toString()),
                    duration = ParserUtils.parseDuration(concertEtDuration.text.toString()),
                    bandId = viewModel.selectedConcert.value!!.bandId,
                    city = concertEtCity.text.toString(),
                    country = concertEtCountry.text.toString(),
                    place = concertEtPlace.text.toString(),
                    concertType = BandItEnums.Concert.Type.values()[typeIndex],
                    id = viewModel.selectedConcert.value!!.id
                )
            )
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