package com.bandit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.builder.AndroidComponents
import com.bandit.databinding.FragmentHomeBinding
import com.bandit.di.DILocator
import com.bandit.ui.band.BandInvitationDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidComponents.header(
                super.requireActivity(),
                homeHeader.headerBtAccount,
                homeHeader.headerBtBand,
                viewLifecycleOwner,
                bandViewModel.band
            )
            // focused in case the use pressed enter to log in
            homeHeader.headerSwitchModes.clearFocus()
            homeHeader.headerTvTitle.setText(R.string.title_home)
            viewModel.generateHomeElements(
                homeSvTableLayout,
                super.requireActivity(),
                super.requireContext(),
                super.requireActivity().findViewById(R.id.main_bottom_navigation_view)
            )
        }
        drawerHeader()
        bandInvitation()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun drawerHeader() {
        // TODO: Fix needed
        /*super.requireActivity().findViewById<TextView>(R.id.drawer_header_tv_name)
            .text = DILocator.database.currentAccount.name
        super.requireActivity().findViewById<TextView>(R.id.drawer_header_tv_email)
            .text = DILocator.database.currentAccount.email
    */
    }

    private fun bandInvitation() {
        if(!DILocator.database.currentBandInvitation.isEmpty()) {
            val bandInvitationDialogFragment = BandInvitationDialogFragment()
            AndroidUtils.showDialogFragment(bandInvitationDialogFragment, childFragmentManager)
        }
    }

}