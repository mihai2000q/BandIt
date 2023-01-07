package com.bandit.ui.concerts

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.builder.adapter.ConcertAdapter
import com.bandit.databinding.FragmentConcertsBinding

class ConcertsFragment : Fragment() {

    private var binding: FragmentConcertsBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()
    private lateinit var detailFragment: ConcertDetailDialogFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcertsBinding.inflate(inflater, container, false)

        binding?.concertsBtAdd?.setOnClickListener {
            ConcertAddDialogFragment().show(childFragmentManager, ConcertAddDialogFragment.TAG)
        }
        binding?.concertsBtFilter?.setOnClickListener {
            ConcertFilterDialogFragment().show(childFragmentManager, ConcertFilterDialogFragment.TAG)
        }
        detailFragment = ConcertDetailDialogFragment()

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.concerts.observe(viewLifecycleOwner) {
            binding?.concertsList?.adapter = ConcertAdapter(it.sorted(), { concert ->
                viewModel.selectedConcert.value = concert
                detailFragment.show(childFragmentManager, ConcertDetailDialogFragment.TAG) },
            { concert -> viewModel.selectedConcert.value = concert; return@ConcertAdapter true },
            { concert -> return@ConcertAdapter viewModel.removeConcert(concert) }) {
                ConcertEditDialogFragment().show(childFragmentManager, ConcertEditDialogFragment.TAG)
                return@ConcertAdapter true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}