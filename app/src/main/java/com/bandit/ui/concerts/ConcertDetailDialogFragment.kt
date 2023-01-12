package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.data.model.Concert
import com.bandit.databinding.DialogFragmentConcertDetailBinding
import com.bandit.constant.Constants

class ConcertDetailDialogFragment : DialogFragment() {

    private var binding: DialogFragmentConcertDetailBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentConcertDetailBinding.inflate(inflater, container, false)

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedConcert.observe(viewLifecycleOwner) { assignConcertDetails(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun assignConcertDetails(concert: Concert) {
        binding?.concertDetailPlace?.text = concert.place
        binding?.concertDetailTitle?.text = concert.name
        binding?.concertDetailCity?.text = concert.city
        binding?.concertDetailCountry?.text = concert.country
    }

    companion object {
        const val TAG = Constants.Concert.DETAIL_CONCERT_TAG
    }

}