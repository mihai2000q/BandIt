package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert

class ConcertEditDialogFragment : ConcertDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerType()
        with(binding) {
            concertButton.setText(R.string.save_button)
            with(viewModel.selectedConcert.value!!) {
                concertEtName.setText(name)
                concertEtCity.setText(city)
                concertEtDate.setText(dateTime.toLocalDate().toString())
                concertEtTime.setText(dateTime.toLocalTime().toString())
                concertEtCountry.setText(country)
                concertEtPlace.setText(place)
                concertEtSpinnerType.setSelection(type.ordinal)
            }

            concertButton.setOnClickListener {
                viewModel.editConcert(
                    Concert(
                        concertEtName.text.toString(),
                        parseDateTime(),
                        concertEtCity.text.toString(),
                        concertEtCountry.text.toString(),
                        concertEtPlace.text.toString(),
                        BandItEnums.Concert.Type.values()[typeIndex],
                        viewModel.selectedConcert.value!!.id,
                        viewModel.selectedConcert.value!!.userUid
                    )
                )
                super.dismiss()
            }
        }

    }

    companion object {
        const val TAG = Constants.Concert.EDIT_CONCERT_TAG
    }
}