package com.bandit.ui.signup

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentSignupBinding
import com.bandit.di.DILocator
import com.bandit.service.IValidatorService
import com.bandit.util.AndroidUtils
import com.google.android.material.snackbar.Snackbar

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by activityViewModels()
    private lateinit var snackbar: Snackbar
    private lateinit var validatorService: IValidatorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validatorService = DILocator.getValidatorService(super.requireActivity())
        with(binding) {
            signupEtPassword.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    signupBtSignup.callOnClick()
                    signupBtSignup.requestFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            signupCbTerms.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked && signupCbTerms.error != null) {
                    signupCbTerms.error = null
                    snackbar.dismiss()
                }
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
            signupBtSignup.setOnClickListener {
                AndroidUtils.loadIntent(this@SignupFragment) { signUpButton() }
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
            if (!viewModel.database.isEmailInUse(binding.signupEtEmail.text.toString()))
                this.signUp()
            else
                binding.signupEtEmail.error = resources.getString(R.string.et_email_validation_email_already_used)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateFields(): Boolean {
        val result = validatorService.validateEmail(binding.signupEtEmail) &&
                validatorService.validatePassword(binding.signupEtPassword)
        if(!result)
            return false
        with(binding) {
            if(!signupCbTerms.isChecked) {
                signupCbTerms.error = resources.getString(R.string.cb_terms_validation)
                snackbar = AndroidComponents.snackbarNotification(
                    binding.signupLayout,
                    resources.getString(R.string.cb_terms_validation),
                    resources.getString(R.string.bt_accept)
                ) {
                    signupCbTerms.isChecked = true
                    signupCbTerms.error = null
                }
                snackbar.show()
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
            signupCbTerms.isChecked = false
        }
    }
}