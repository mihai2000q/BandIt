package com.bandit.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        viewModel.generateHomeElements(
            viewModel.elements.value ?: mapOf(),
            binding!!.homeSvTableLayout,
            this.requireContext(),
            this.requireActivity().findViewById(R.id.main_bottom_navigation_view)
        )

        binding?.homeBtAccount?.setOnClickListener{
            HomeAccountDialogFragment().show(childFragmentManager, HomeAccountDialogFragment.TAG)
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}