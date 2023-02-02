package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.util.AndroidUtils
import com.bandit.util.ParserUtils
import java.time.Duration

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
                concertEtSpinnerType.setSelection(concertType?.ordinal ?: 0)
            }

            concertButton.setOnClickListener {
                viewModel.editConcert(
                    Concert(
                        name = concertEtName.text.toString(),
                        dateTime = ParserUtils.parseDateTime(concertEtDate.text.toString(),
                            concertEtTime.text.toString()),
                        duration = Duration.ZERO,
                        bandId = viewModel.selectedConcert.value!!.bandId,
                        city = concertEtCity.text.toString(),
                        country = concertEtCountry.text.toString(),
                        place = concertEtPlace.text.toString(),
                        concertType =BandItEnums.Concert.Type.values()[typeIndex],
                        id = viewModel.selectedConcert.value!!.id
                    )
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.concert_edit_toast)
                )
                super.dismiss()
            }
        }

    }

    companion object {
        const val TAG = Constants.Concert.EDIT_TAG
    }
}