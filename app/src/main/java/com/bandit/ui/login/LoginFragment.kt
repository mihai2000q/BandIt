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
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentLoginBinding
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            fragmentLoginCbRemember.isChecked = AndroidUtils.getBooleanPreference(
                super.requireActivity(), Constants.Preferences.REMEMBER_ME
            )

            with(viewModel) {
                email.observe(viewLifecycleOwner) { fragmentLoginEtUsername.setText(it) }
                fragmentLoginBtLogin.setOnClickListener {
                    signInWithEmailAndPassword(
                        fragmentLoginEtUsername.text.toString(),
                        fragmentLoginEtPassword.text.toString()
                        ) {
                        loginNavigationUnlocked()
                        AndroidUtils.savePreference(
                            super.requireActivity(),
                            Constants.Preferences.REMEMBER_ME,
                            binding.fragmentLoginCbRemember.isChecked
                        )
                    }
                }
            }

            fragmentLoginBtSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loginNavigationUnlocked() {
        this.requireActivity().findViewById<BottomNavigationView>(R.id.main_bottom_navigation_view)
            ?.visibility = View.VISIBLE
        this.requireActivity().findViewById<DrawerLayout>(R.id.main_drawer_layout)
            ?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

}