package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.ui.concerts.ConcertsViewModel.*
import com.bandit.util.AndroidUtils
import java.time.LocalDate
import java.time.LocalTime

class ConcertFilterDialogFragment : ConcertDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val map = mapOf(
            binding.concertEtName to Filter.Name,
            binding.concertEtDate to Filter.Date,
            binding.concertEtTime to Filter.Time,
            binding.concertEtCity to Filter.City,
            binding.concertEtCountry to Filter.Country,
            binding.concertEtPlace to Filter.Place,
        )
        with(binding) {
            concertEtSpinnerType.visibility = View.GONE // bug
            concertButton.setText(R.string.bt_filter)
            with(viewModel.filters.value) {
                map.forEach { (key, value) -> key.setText(this?.get(value)) }
                /* TODO: fix the filtering for concert type
                    it should not force the user to always use it */
                /*val type = this?.get(Filter.Type)
                concertEtSpinnerType.setSelection(
                    if(type.isNullOrEmpty()) 0 else type.toInt()
                )*/
            }

            concertButton.setOnClickListener {
                viewModel.filterConcerts(
                    concertEtName.text.toString(),
                    if(concertEtDate.text.toString().isEmpty())
                        null
                    else
                        LocalDate.parse(concertEtDate.text.toString()),
                    if(concertEtTime.text.toString().isEmpty())
                        null
                    else
                        LocalTime.parse(concertEtTime.text.toString()),
                    concertEtCity.text.toString(),
                    concertEtCountry.text.toString(),
                    concertEtPlace.text.toString(),
                    //BandItEnums.Concert.Type.values()[typeIndex] 
                    null
                )
                map.forEach { (key, value) ->
                    viewModel.filters.value?.replace(value, key.text.toString())
                }
                viewModel.filters.value?.replace(Filter.Type, typeIndex.toString())
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.Concert_Filter_Toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Concert.FILTER_CONCERT_TAG
    }
}