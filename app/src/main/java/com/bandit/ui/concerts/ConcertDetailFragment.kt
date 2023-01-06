package com.bandit.ui.concerts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.data.model.Concert
import com.bandit.databinding.FragmentConcertDetailBinding
import kotlinx.coroutines.selects.select

class ConcertDetailFragment : Fragment() {

    private var binding: FragmentConcertDetailBinding? = null
    private val concertViewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConcertDetailBinding.inflate(inflater, container, false)

        binding?.concertDetailBack?.setOnClickListener {
            findNavController().navigate(R.id.action_concertDetailFragment_to_navigation_concerts)
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        concertViewModel.selectedConcert.observe(viewLifecycleOwner) { assignConcertDetails(it) }
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

}