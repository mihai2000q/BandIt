package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.data.model.Concert
import com.bandit.databinding.FragmentConcertAddBinding
import com.bandit.helper.Constants
import java.time.LocalDateTime

class ConcertAddDialogFragment : DialogFragment() {

    var binding: FragmentConcertAddBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcertAddBinding.inflate(inflater, container, false)
        binding?.concertBtAdd?.setOnClickListener {
            viewModel.addConcert(
                Concert(
                    binding?.concertAddName?.text.toString(),
                    LocalDateTime.now().plusDays(5),
                    binding?.concertAddCity?.text.toString(),
                    binding?.concertAddCountry?.text.toString(),
                    "",
                    Concert.Type.Simple
                )
            )
            viewModel.closeAddDialog?.invoke()
            binding?.concertAddName?.setText("")
            binding?.concertAddCity?.setText("")
            binding?.concertAddCountry?.setText("")
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = Constants.ADD_CONCERT_TAG
    }
}