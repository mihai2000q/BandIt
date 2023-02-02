package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
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
                concertEtSpinnerType.setSelection(concertType?.ordinal ?: 0)
            }

            concertButton.setOnClickListener {
                viewModel.editConcert(
                    Concert(
                        concertEtName.text.toString(),
                        ParserUtils.parseDateTime(concertEtDate.text.toString(), concertEtTime.text.toString()),
                        viewModel.selectedConcert.value!!.bandId,
                        concertEtCity.text.toString(),
                        concertEtCountry.text.toString(),
                        concertEtPlace.text.toString(),
                        BandItEnums.Concert.Type.values()[typeIndex],
                        viewModel.selectedConcert.value!!.id
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