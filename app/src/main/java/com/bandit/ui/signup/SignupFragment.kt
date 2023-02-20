package com.bandit.ui.signup

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
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentSignupBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by activityViewModels()
    private val _database = DILocator.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            signupEtPassword.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    signupButton.callOnClick()
                    signupButton.requestFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            signupCbTerms.setOnClickListener {
                AndroidUtils.hideKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, signupCbTerms)
            }
            viewModel.email.observe(viewLifecycleOwner) {
                signupEtEmail.setText(it)
            }
            signupBtGoBack.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            }
            signupButton.setOnClickListener {
                AndroidUtils.loadTask(this@SignupFragment) { signUpButton() }
            }
        }
    }

    private suspend fun signUpButton() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.signupTitle
        )
        if (validateFields()) {
            if (!_database.isEmailInUse(binding.signupEtEmail.text.toString()))
                signUp()
            else
                binding.signupEtEmail.error = resources.getString(R.string.et_email_validation_email_already_used)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateFields(): Boolean {
        with(binding) {
            if(signupEtEmail.text.isNullOrEmpty()) {
                signupEtEmail.error = resources.getText(R.string.et_email_validation_empty)
                return false
            }
            if(!Patterns.EMAIL_ADDRESS.matcher(signupEtEmail.text).matches()) {
                signupEtEmail.error = resources.getText(R.string.et_email_validation_email)
                return false
            }
            if(signupEtPassword.text.isNullOrEmpty()) {
                signupEtPassword.error = resources.getText(R.string.et_pass_validation_empty)
                return false
            }
            if(signupEtPassword.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
                signupEtPassword.error = resources.getText(R.string.et_pass_validation_minimum)
                return false
            }
            if(!signupCbTerms.isChecked) {
                signupCbTerms.error = resources.getString(R.string.cb_terms_validation)
                AndroidComponents.snackbarNotification(
                    binding.signupLayout,
                    resources.getString(R.string.cb_terms_validation),
                    resources.getString(R.string.bt_accept)
                ) {
                    signupCbTerms.isChecked = true
                    signupCbTerms.error = null
                }
                return false
            }
        }
        return true
    }
    private suspend fun signUp() {
        with(binding) {
            viewModel.email.value = signupEtEmail.text.toString()
            viewModel.createUser(signupEtPassword.text.toString())
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.sign_up_toast),
                Toast.LENGTH_LONG
            )
            signupEtEmail.setText("")
            signupEtPassword.setText("")
        }
    }
}