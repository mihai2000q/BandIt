package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.DialogFragmentConcertFilterBinding
import com.bandit.helper.Constants

class ConcertFilterDialogFragment : DialogFragment() {

    private var binding: DialogFragmentConcertFilterBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentConcertFilterBinding.inflate(
            layoutInflater, container, false
        )

        with(viewModel.filters) {
            binding?.concertFilterName?.setText(value?.get(ConcertsViewModel.Filter.Name))
            binding?.concertFilterCity?.setText(value?.get(ConcertsViewModel.Filter.City))
            binding?.concertFilterCountry?.setText(value?.get(ConcertsViewModel.Filter.Country))
        }

        binding?.concertFilterButton?.setOnClickListener {
            viewModel.filterConcerts(
                binding?.concertFilterName?.text.toString(),
                binding?.concertFilterCity?.text.toString(),
                binding?.concertFilterCountry?.text.toString(),
            )
            with(viewModel.filters) {
                value?.replace(ConcertsViewModel.Filter.Name, binding?.concertFilterName?.text.toString())
                value?.replace(ConcertsViewModel.Filter.City, binding?.concertFilterCity?.text.toString())
                value?.replace(ConcertsViewModel.Filter.Country, binding?.concertFilterCountry?.text.toString())
            }
            this.dismiss()
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = Constants.Concert.FILTER_CONCERT_TAG
    }
}