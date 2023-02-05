package com.bandit.ui.login

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentLoginBinding
import com.bandit.di.DILocator
import com.bandit.ui.first.login.FirstLoginViewModel
import com.bandit.util.AndroidUtils
import com.bandit.util.PreferencesUtils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
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
            viewModel.email.observe(viewLifecycleOwner) { loginEtEmail.setText(it) }
            loginEtPassword.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loginBtLogin.callOnClick()
                    loginBtLogin.requestFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            loginCbRemember.setOnClickListener {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    loginCbRemember
                )
            }
            loginBtLogin.setOnClickListener {
                lifecycleScope.launch {
                    runBlocking {
                        viewModel.signInWithEmailAndPassword(
                            loginEtEmail.text.toString(),
                            loginEtPassword.text.toString(),
                            { login() }
                        )
                    }
                }
            }
            loginBtSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun login() {
        var result: Boolean? = null
        lifecycleScope.launch {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                binding.loginEtPassword
            )
            launch { result = DILocator.database.isUserAccountSetup() }.join()

            if (result == true) {
                launch { DILocator.database.init() }.join()
                PreferencesUtils.savePreference(
                    super.requireActivity(),
                    Constants.Preferences.REMEMBER_ME,
                    binding.loginCbRemember.isChecked
                )
                AndroidUtils.unlockNavigation(
                    super.requireActivity().findViewById(R.id.main_bottom_navigation_view),
                    super.requireActivity().findViewById(R.id.main_drawer_layout)
                )
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else if (result == false) {
                findNavController().navigate(R.id.action_navigation_login_to_firstLoginFragment)
                val firstLoginViewModel: FirstLoginViewModel by activityViewModels()
                firstLoginViewModel.rememberMe.value = binding.loginCbRemember.isChecked
            }
            AndroidUtils.toastNotification(
                super.requireContext(),
                resources.getString(R.string.login_toast),
                Toast.LENGTH_LONG
            )
        }
    }
}