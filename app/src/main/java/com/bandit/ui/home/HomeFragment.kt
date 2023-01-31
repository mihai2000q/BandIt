package com.bandit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.databinding.FragmentHomeBinding
import com.bandit.di.DILocator
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.BandInvitationDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.band.CreateBandDialogFragment
import com.bandit.util.AndroidUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val createBandDialogFragment = CreateBandDialogFragment()
        val bandDialogFragment = BandDialogFragment()
        val accountDialogFragment = AccountDialogFragment(binding.homeBtAccount)
        with(binding) {
            AndroidUtils.accountButton(
                super.requireActivity(),
                homeBtAccount,
                accountDialogFragment
            )
            AndroidUtils.bandButton(
                super.requireActivity(),
                homeBtBand,
                bandViewModel.band,
                viewLifecycleOwner,
                createBandDialogFragment,
                bandDialogFragment
            )
            viewModel.generateHomeElements(
                homeSvTableLayout,
                super.requireActivity(),
                super.requireContext(),
                super.requireActivity().findViewById(R.id.main_bottom_navigation_view)
            )

            homeTvWelcome.text = buildString {
                append("Welcome ")
                append(DILocator.database.currentAccount.nickname)
                append(", to")
            }
        }
        bandInvitation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun bandInvitation() {
        if(!DILocator.database.currentBandInvitation.isEmpty()) {
            val bandInvitationDialogFragment = BandInvitationDialogFragment()
            AndroidUtils.showDialogFragment(bandInvitationDialogFragment, childFragmentManager)
        }

    }

}