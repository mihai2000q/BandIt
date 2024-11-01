package com.bandit.ui.concerts

import android.os.Bundle
import android.view.View
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.concerts.ConcertsViewModel.Filter
import com.bandit.ui.template.ConcertDialogFragment
import com.bandit.util.ParserUtils
import com.google.android.material.badge.BadgeDrawable
import java.time.LocalDate
import java.time.LocalTime

class ConcertFilterDialogFragment(private val badgeDrawable: BadgeDrawable) : ConcertDialogFragment() {

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
                /*TODO: fix the filtering for concert type
                    it should not force the user to always use it */
                /*val type = this?.get(Filter.Type)
                concertEtSpinnerType.setSelection(
                    if(type.isNullOrEmpty()) 0 else type.toInt()
                )*/
            }

            concertButton.setOnClickListener {
                viewModel.filterConcerts(
                    name = concertEtName.text.toString(),
                    date = if(concertEtDate.text.isNullOrEmpty())
                        null
                    else
                        LocalDate.parse(concertEtDate.text.toString()),
                    time = if(concertEtTime.text.isNullOrEmpty())
                        null
                    else
                        LocalTime.parse(concertEtTime.text.toString()),
                    duration =
                    if(concertEtDuration.text.isNullOrEmpty())
                        null
                    else
                        ParserUtils.parseDurationTextToHoursAndMinutes(concertEtDuration.text.toString()),
                    city = concertEtCity.text.toString(),
                    country = concertEtCountry.text.toString(),
                    place = concertEtPlace.text.toString(),
                    //type = BandItEnums.Concert.Type.values()[typeIndex]
                )
                map.forEach { (key, value) ->
                    viewModel.filters.value?.replace(value, key.text.toString())
                }
                //viewModel.filters.value?.replace(Filter.Type, typeIndex.toString())
                badgeDrawable.number = viewModel.getFiltersOn()
                badgeDrawable.isVisible = viewModel.getFiltersOn() > 0
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.concert_filter_toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Concert.FILTER_TAG
    }
}