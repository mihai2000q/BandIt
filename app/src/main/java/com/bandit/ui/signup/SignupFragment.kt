package com.bandit.ui.signup

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.databinding.FragmentSignupBinding
import com.bandit.util.AndroidUtils

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SignupViewModel by activityViewModels()
    private var phase = 0

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
            signupEtPassword.visibility = View.GONE
            signupBtCancel.setOnClickListener {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            }
            signupBtNext.setOnClickListener { signUpBtNext() }
            signupBtNext.isEnabled = false
            signupEtString.addTextChangedListener {
                signupBtNext.isEnabled = it.toString().isNotEmpty()
            }
            signupEtPassword.addTextChangedListener {
                signupBtNext.isEnabled = it.toString().isNotEmpty()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signUpBtNext() {
        with(binding) {
            when(phase) {
                0 ->  {
                    viewModel.email.value = signupEtString.text.toString()
                    signupEtPassword.visibility = View.VISIBLE
                    signupEtString.visibility = View.GONE
                    phase(resources.getString(R.string.tv_password))
                }
                1 -> {
                    viewModel.password.value = signupEtPassword.text.toString()
                    signupEtPassword.visibility = View.GONE
                    signupEtString.visibility = View.GONE
                    signupBtNext.visibility = View.GONE
                    signupBtCancel.setText(R.string.bt_go_back)
                    phase(resources.getString(R.string.sign_up_tv_last))
                    signup()
                }
                else -> {}
            }
        }
    }

    private fun phase(subjectText: String) {
        with(binding) {
            signupTvSubject.text = subjectText
            signupProgressBar.progress++
            phase++
        }
    }

    private fun signup() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.signupTitle
        )
        viewModel.createUser()
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.Signup_Toast),
            Toast.LENGTH_LONG
        )
    }

}