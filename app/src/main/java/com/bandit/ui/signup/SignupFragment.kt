package com.bandit.ui.signup

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentSignupBinding
import com.bandit.util.AndroidUtils

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by activityViewModels()

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
            viewModel.email.observe(viewLifecycleOwner) {
                signupEtEmail.setText(it)
            }
            signupBtGoBack.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            }
            signupButton.setOnClickListener {
                if(validateFields())
                    signUp()
            }
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
        }
        return true
    }
    private fun signUp() {
        with(binding) {
            AndroidUtils.hideKeyboard(
                super.requireActivity(),
                Context.INPUT_METHOD_SERVICE,
                binding.signupTitle
            )
            viewModel.email.value = signupEtEmail.text.toString()
            viewModel.createUser(signupEtPassword.text.toString())
            AndroidUtils.toastNotification(
                super.requireContext(),
                resources.getString(R.string.sign_up_toast),
                Toast.LENGTH_LONG
            )
            signupEtEmail.setText("")
            signupEtPassword.setText("")
        }
    }
}