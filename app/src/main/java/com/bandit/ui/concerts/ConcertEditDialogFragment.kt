package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.BandItEnums
import com.bandit.data.model.Concert
import com.bandit.databinding.DialogFragmentConcertEditBinding
import java.time.LocalDateTime
import com.bandit.constant.Constants

class ConcertEditDialogFragment : DialogFragment() {

    private var binding: DialogFragmentConcertEditBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentConcertEditBinding.inflate(layoutInflater, container, false)

        binding?.concertEditName?.setText(viewModel.selectedConcert.value?.name)
        binding?.concertEditCity?.setText(viewModel.selectedConcert.value?.city)
        binding?.concertEditCountry?.setText(viewModel.selectedConcert.value?.country)
        binding?.concertEditPlace?.setText(viewModel.selectedConcert.value?.place)

        binding?.concertEditSaveBt?.setOnClickListener {
            viewModel.editConcert(
                Concert(
                    binding?.concertEditName?.text.toString(),
                    LocalDateTime.now(),
                    binding?.concertEditCity?.text.toString(),
                    binding?.concertEditCountry?.text.toString(),
                    binding?.concertEditPlace?.text.toString(),
                    BandItEnums.Concert.Type.Simple,
                    viewModel.selectedConcert.value?.id ?: -1,
                    viewModel.selectedConcert.value?.userUid
                )
            )
            this.dismiss()
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = Constants.Concert.EDIT_CONCERT_TAG
    }
}