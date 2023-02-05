package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.databinding.DialogFragmentConcertDetailBinding
import com.bandit.extension.normalizeWord
import com.bandit.extension.print
import com.bandit.util.AndroidUtils

class ConcertDetailDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentConcertDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentConcertDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedConcert.observe(viewLifecycleOwner) { assignConcertDetails(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assignConcertDetails(concert: Concert) {
        with(binding) {
            concertDetailTitle.text = concert.name
            concertDetailDateTime.text = concert.dateTime.print()
            AndroidUtils.ifNullHide(concertDetailCity, concert.city)
            AndroidUtils.ifNullHide(concertDetailCountry, concert.country)
            AndroidUtils.ifNullHide(concertDetailPlace, concert.place)
            AndroidUtils.ifNullHide(concertDetailType, concert.concertType?.name?.normalizeWord())
        }
    }

    companion object {
        const val TAG = Constants.Concert.DETAIL_TAG
    }

}