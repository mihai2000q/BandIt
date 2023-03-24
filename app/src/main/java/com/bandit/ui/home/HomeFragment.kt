package com.bandit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.databinding.FragmentHomeBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.HomeButtonAdapter
import com.bandit.util.AndroidUtils

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val accountViewModel: AccountViewModel by activityViewModels()
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
            AndroidUtils.setupRefreshLayout(this@HomeFragment, homeRvButtons)
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
            ?.text = accountViewModel.account.value!!.name
        super.requireActivity().findViewById<TextView>(R.id.drawer_header_tv_email)
            ?.text = accountViewModel.account.value!!.email
    }

}