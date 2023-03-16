package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.data.model.Concert
import com.bandit.databinding.DialogFragmentConcertDetailBinding
import com.bandit.extension.normalizeWord
import com.bandit.extension.print
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ConcertDetailDialogFragment : BottomSheetDialogFragment() {

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
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        viewModel.selectedConcert.observe(viewLifecycleOwner) { assignConcertDetails(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun assignConcertDetails(concert: Concert) {
        with(binding) {
            concertDetailName.text = concert.name
            concertDetailDateTime.text = concert.dateTime.print()
            AndroidUtils.ifNullHide(concertDetailCity, concertDetailTvCity, concert.city)
            AndroidUtils.ifNullHide(concertDetailCountry, concertDetailTvCountry, concert.country)
            AndroidUtils.ifNullHide(concertDetailPlace, concertDetailTvPlace, concert.place)
            concertDetailDuration.text = concert.duration.print()
            AndroidUtils.ifNullHide(concertDetailConcertType, concertDetailTvType,
                concert.concertType.name.normalizeWord())
        }
    }

    companion object {
        const val TAG = Constants.Concert.DETAIL_TAG
    }

}