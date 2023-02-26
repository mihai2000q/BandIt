package com.bandit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.BandItEnums
import com.bandit.databinding.FragmentHomeBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.HomeButtonAdapter
import com.bandit.ui.band.BandInvitationDialogFragment
import com.bandit.util.AndroidUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val _database = DILocator.getDatabase()
    private val _buttons = mapOf(
        "Concerts" to BandItEnums.Home.NavigationType.Bottom,
        "Songs" to BandItEnums.Home.NavigationType.Bottom,
        "Social" to BandItEnums.Home.NavigationType.Bottom,
        "Schedule" to BandItEnums.Home.NavigationType.Bottom,
    )

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
                homeHeader.headerBtAccount
            )
            homeHeader.headerTvTitle.setText(R.string.title_home)
            homeRvButtons.adapter = HomeButtonAdapter(
                this@HomeFragment,
                _buttons,
                super.requireActivity().findViewById(R.id.main_bottom_navigation_view)
            )
        }
        drawerHeader()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun drawerHeader() {
        super.requireActivity().findViewById<TextView>(R.id.drawer_header_tv_name)
            ?.text = _database.currentAccount.name
        super.requireActivity().findViewById<TextView>(R.id.drawer_header_tv_email)
            ?.text = _database.currentAccount.email
    }

}