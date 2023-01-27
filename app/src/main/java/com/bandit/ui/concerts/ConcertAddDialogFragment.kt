package com.bandit.ui.concerts

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils

class ConcertAddDialogFragment : ConcertDialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerType()
        with(binding) {
            concertButton.setText(R.string.add_button)
            concertButton.setOnClickListener {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    concertButton
                )
                viewModel.addConcert(
                    Concert(
                        concertEtName.text.toString(),
                        parseDateTime(),
                        concertEtCity.text.toString(),
                        concertEtCountry.text.toString(),
                        concertEtPlace.text.toString(),
                        BandItEnums.Concert.Type.values()[typeIndex],
                        DILocator.database.currentBand.id
                    )
                )
                AndroidUtils.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.Concert_Add_Toast)
                )
                super.dismiss()
            }
        }
    }

    companion object {
        const val TAG = Constants.Concert.ADD_CONCERT_TAG
    }
}