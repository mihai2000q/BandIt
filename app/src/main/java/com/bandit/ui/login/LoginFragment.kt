package com.bandit.ui.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.repository.AccountRepository
import com.bandit.databinding.FragmentLoginBinding
import com.bandit.di.DILocator
import com.bandit.ui.component.AndroidComponents
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()
    private val validatorService by lazy { DILocator.getValidatorService(super.requireActivity()) }
    private val preferencesService by lazy { DILocator.getPreferencesService(super.requireActivity()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inflater = TransitionInflater.from(requireContext())
        exitTransition = inflater.inflateTransition(R.transition.slide_left)
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

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
            loginCbRemember.setOnClickListener {
                AndroidUtils.hideKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, loginCbRemember)
            }
            loginBtLogin.setOnClickListener {
                lifecycleScope.launch {
                    AndroidUtils.hideKeyboard(
                        super.requireActivity(),
                        Context.INPUT_METHOD_SERVICE,
                        loginEtPassword
                    )
                    val destination = AndroidUtils.loadIntentWithDestination(
                        this@LoginFragment) { tryToLogin() }
                    super.requireActivity().whenStarted { navigation(destination) }
                }
            }
            loginBtSignup.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }
        }
        if(this.arguments?.getBoolean(Constants.SafeArgs.FAIL_LOGIN_NETWORK) == true)
            onNetworkFailure()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun tryToLogin(): Boolean? {
        var result: Boolean? = null
        with(binding) {
            if(validateFields()) {
                if(AndroidUtils.isNetworkAvailable()) {
                    if (viewModel.database.isEmailInUse(loginEtEmail.text.toString())) {
                        loginEtEmailLayout.error = null
                        viewModel.signInWithEmailAndPassword(
                            loginEtEmail.text.toString(),
                            loginEtPassword.text.toString(),
                            { result = this@LoginFragment.loginOnSuccess() }
                        ) { this@LoginFragment.onLoginFailure() }
                    } else {
                        loginEtEmailLayout.error =
                            resources.getString(R.string.et_email_validation_email_not_used)
                        loginEtPassword.setText("")
                    }
                }
                else
                    this@LoginFragment.onNetworkFailure()
            }
        }
        return result
    }

    private suspend fun loginOnSuccess(): Boolean? {
        //TODO: Remove comment, but for debugging purposes this will be deactivated
        /*return validatorService.validateEmailVerified(
            binding.loginEtEmail,
            binding.loginEtEmailLayout
            { login() },
            viewModel.auth
        )*/
        return login()
    }

    private fun onLoginFailure() {
        with(binding) {
            loginEtPasswordLayout.error = resources.getString(R.string.et_pass_validation_incorrect)
            loginEtPassword.setText("")
        }
    }

    private fun onNetworkFailure() {
        binding.loginEtPassword.setText("")
        AndroidComponents.snackbarNotification(
            binding.loginLayout,
            resources.getString(R.string.no_internet_connection_snackbar),
            resources.getString(R.string.bt_retry)
        ).show()
    }

    private fun validateFields(): Boolean {
        return  validatorService.validateEmail(binding.loginEtEmail, binding.loginEtEmailLayout) &&
                validatorService.validatePassword(binding.loginEtPassword, binding.loginEtPasswordLayout)
    }

    private suspend fun login(): Boolean? = coroutineScope {
        async {
            val result = AccountRepository.isUserAccountSetup(
                viewModel.database,
                viewModel.auth.currentUser!!.uid
            )
            if (result == true) {
                viewModel.database.init(viewModel.auth.currentUser!!.uid)
                preferencesService.savePreference(
                    Constants.Preferences.REMEMBER_ME,
                    binding.loginCbRemember.isChecked
                )
                AndroidUtils.unlockNavigation(super.requireActivity())
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.login_toast),
                    Toast.LENGTH_LONG
                )
            }
            return@async result
        }
    }.await()

    private fun navigation(boolean: Boolean?) {
        if (boolean == true)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        else if (boolean == false)
            findNavController().navigate(R.id.action_navigation_login_to_firstLoginFragment,
                bundleOf(Constants.SafeArgs.REMEMBER_ME to binding.loginCbRemember.isChecked))
    }
}