package com.bandit.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.databinding.FragmentLoginBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding?.fragmentLoginBtLogin?.setOnClickListener { login() }
        binding?.fragmentLoginBtSignup?.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun login() {
        this.activity?.findViewById<BottomNavigationView>(R.id.main_bottom_navigation_view)
            ?.visibility = View.VISIBLE
        this.activity?.findViewById<DrawerLayout>(R.id.main_drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}