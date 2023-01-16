package com.bandit.ui.signup

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        }
    }

    @SuppressLint("SetTextI18n")
    private fun signUpBtNext() {
        with(binding) {
            when(phase) {
                0 ->  {
                    viewModel.email.value = signupEtString.text.toString()
                    signupEtString.setText("")
                    signupEtPassword.visibility = View.VISIBLE
                    signupEtString.visibility = View.GONE
                    signupTvSubject.text = "Password:"
                    signupProgressBar.progress++
                    phase++
                }
                1 -> {
                    viewModel.password.value = signupEtPassword.text.toString()
                    signupEtPassword.visibility = View.GONE
                    signupTvSubject.text = "Nickname:"
                    signupEtString.visibility = View.VISIBLE
                    signupProgressBar.progress++
                    phase++
                }
                2 -> {
                    viewModel.displayName.value = signupEtString.text.toString()
                    signupEtString.visibility = View.GONE
                    signupBtNext.visibility = View.GONE
                    signupBtCancel.text = "Go Back"
                    signupTvSubject.text = "A confirmation email has been sent"
                    signupProgressBar.progress++
                    phase++
                    viewModel.createUser()
                    AndroidUtils.hideKeyboard(
                        super.requireActivity(),
                        Context.INPUT_METHOD_SERVICE,
                        signupTitle
                    )
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}