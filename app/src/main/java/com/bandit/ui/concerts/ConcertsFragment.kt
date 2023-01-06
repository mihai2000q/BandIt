package com.bandit.ui.concerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bandit.builder.adapter.ConcertAdapter
import com.bandit.databinding.FragmentConcertsBinding

class ConcertsFragment : Fragment() {

    private var binding: FragmentConcertsBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcertsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.concerts.observe(viewLifecycleOwner) {
            binding?.concertsList?.adapter = ConcertAdapter(it.sorted()) { concert ->
                viewModel.selectedConcert.value = concert
                findNavController()
                    .navigate(com.bandit.R.id.action_navigation_concerts_to_concertDetailFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}