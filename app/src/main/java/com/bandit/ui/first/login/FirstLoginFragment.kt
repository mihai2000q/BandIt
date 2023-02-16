package com.bandit.ui.first.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentFirstLoginBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.bandit.util.PreferencesUtils
import kotlinx.coroutines.launch

class FirstLoginFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLoginViewModel by activityViewModels()
    private val _database = DILocator.database
    private var phase = 0
    private var roleIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerRole()
        with(binding) {
            firstLoginBtCancel.setOnClickListener {
                findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_login)
            }
            firstLoginBtNext.setOnClickListener {
                lifecycleScope.launch {
                    if(AndroidUtils.loadTaskWithDestination(this@FirstLoginFragment) { firstLoginBtNext() } == true)
                        super.requireActivity().whenStarted {
                            findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_home)
                        }
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun firstLoginBtNext(): Boolean? {
        with(binding) {
            when (phase) {
                0 -> {
                    if (firstLoginEtName.text.isNullOrEmpty()) {
                        firstLoginEtName.error = resources.getString(R.string.et_name_validation)
                        return false
                    }
                    viewModel.name.value = firstLoginEtName.text.toString()
                    flip()
                    return false
                }
                1 -> {
                    if (firstLoginEtNickname.text.isNullOrEmpty()) {
                        firstLoginEtNickname.error = resources.getString(R.string.et_nickname_validation)
                        return false
                    }
                    viewModel.nickname.value = firstLoginEtNickname.text.toString()
                    flip()
                    return false
                }
                2 -> {
                    viewModel.role.value = BandItEnums.Account.Role.values()[roleIndex]
                    createAccount()
                    firstLoginBtCancel.visibility = View.GONE
                    firstLoginBtNext.setText(R.string.first_login_bt_next_last)
                    flip()
                }
                3 -> return goToHomePage()
                else -> {}
            }
        }
        return null
    }

    private fun flip() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.firstLoginTitle
        )
        binding.firstLoginVfForm.showNext()
        binding.firstLoginProgressBar.progress++
        phase++
    }

    private suspend fun goToHomePage(): Boolean {
        _database.init()
        PreferencesUtils.savePreference(
            super.requireActivity(),
            Constants.Preferences.REMEMBER_ME,
            this.arguments?.getBoolean(Constants.SafeArgs.REMEMBER_ME) ?: false
        )
        AndroidUtils.unlockNavigation(
            super.requireActivity().findViewById(R.id.main_bottom_navigation_view),
            super.requireActivity().findViewById(R.id.main_drawer_layout)
        )
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.first_login_toast)
        )
        return true
    }

    private fun spinnerRole() {
        with(binding) {
            val adapter = ArrayAdapter(
                super.requireContext(),
                android.R.layout.simple_spinner_item,
                BandItEnums.Account.Role.values()
            )
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            firstLoginSpinnerRole.adapter = adapter
            firstLoginSpinnerRole.onItemSelectedListener = this@FirstLoginFragment
        }
    }

    private suspend fun createAccount() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.firstLoginTitle
        )
        viewModel.createAccount()
        _database.setUserAccountSetup(true)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        roleIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}