package com.bandit.ui.concerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.databinding.FragmentConcertsBinding
import com.bandit.ui.AccountDialogFragment
import com.bandit.util.AndroidUtils

class ConcertsFragment : Fragment() {

    private var _binding: FragmentConcertsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.concertsBtAdd.setOnClickListener {
            ConcertAddDialogFragment().show(childFragmentManager, ConcertAddDialogFragment.TAG)
        }
        binding.concertsBtFilter.setOnClickListener {
            ConcertFilterDialogFragment().show(childFragmentManager, ConcertFilterDialogFragment.TAG)
        }
        binding.concertsBtAccount.setOnClickListener {
            AccountDialogFragment().show(childFragmentManager, AccountDialogFragment.TAG)
        }

        viewModel.concerts.observe(viewLifecycleOwner) {
            binding.concertsList.adapter = ConcertAdapter(it.sorted(), { concert ->
                viewModel.selectedConcert.value = concert
                ConcertDetailDialogFragment().show(childFragmentManager, ConcertDetailDialogFragment.TAG) },
                { concert -> viewModel.selectedConcert.value = concert; return@ConcertAdapter true },
                { concert ->
                    AndroidUtils.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.Concert_Remove_Toast),
                    )
                    return@ConcertAdapter viewModel.removeConcert(concert)
                }) { concert ->
                viewModel.selectedConcert.value = concert
                ConcertEditDialogFragment().show(childFragmentManager, ConcertEditDialogFragment.TAG)
                return@ConcertAdapter true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}