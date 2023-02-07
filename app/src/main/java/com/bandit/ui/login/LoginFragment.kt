package com.bandit.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Patterns
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
    private val _database = DILocator.database

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
            //press enter to login
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
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    binding.loginEtPassword
                )
                lifecycleScope.launch {
                    loginButtonOnClick()
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

    private suspend fun loginButtonOnClick() {
        with(binding) {
            if(validateFields()) {
                if(_database.isEmailInUse(loginEtEmail.text.toString())) {
                    runBlocking {
                        viewModel.signInWithEmailAndPassword(
                            loginEtEmail.text.toString(),
                            loginEtPassword.text.toString(),
                            { loginOnSuccess() }
                        ) { loginOnFailure() }
                    }
                }
                else {
                    loginEtEmail.error = resources.getString(R.string.et_email_validation_email_not_used)
                    loginEtPassword.setText("")
                }
            }
        }
    }

    private fun loginOnSuccess() {
        // TODO: Remove comment, but for debugging purposes this will be deactivated
        /*if (_auth.currentUser!!.isEmailVerified)
            login()
        else {
            binding.loginEtEmail.error =
                resources.getString(R.string.et_email_validation_email_verified)
            _auth.signOut()
        }*/
        login()
    }

    private fun loginOnFailure() {
        with(binding) {
            loginEtPassword.error = resources.getString(R.string.et_pass_validation_incorrect)
            loginEtPassword.setText("")
        }
    }

    private fun validateFields(): Boolean {
        with(binding) {
            if(loginEtEmail.text.isNullOrEmpty()) {
                loginEtEmail.error = resources.getText(R.string.et_email_validation_empty)
                return false
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(loginEtEmail.text).matches()) {
                loginEtEmail.error = resources.getText(R.string.et_email_validation_email)
                return false
            }
            if(loginEtPassword.text.isNullOrEmpty()) {
                loginEtPassword.error = resources.getText(R.string.et_pass_validation_empty)
                return false
            }
            if(loginEtPassword.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
                loginEtPassword.error = resources.getText(R.string.et_pass_validation_minimum)
                return false
            }
        }
        return true
    }

    private fun login() {
        var result: Boolean? = null
        lifecycleScope.launch {
            launch { result = _database.isUserAccountSetup() }.join()

            if (result == true) {
                launch { _database.init() }.join()
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
                // TODO: use safe args instead
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