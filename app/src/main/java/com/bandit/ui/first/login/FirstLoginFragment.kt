package com.bandit.ui.first.login

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
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
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.component.ImagePickerDialog
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.databinding.FragmentFirstLoginBinding
import com.bandit.di.DILocator
import com.bandit.service.IPreferencesService
import com.bandit.service.IValidatorService
import com.bandit.ui.account.AccountViewModel
import com.bandit.util.AndroidUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FirstLoginFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FirstLoginViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()
    private var phase = 0
    private var roleIndex = 0
    private lateinit var validatorService: IValidatorService
    private lateinit var preferencesService: IPreferencesService

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
        validatorService = DILocator.getValidatorService(super.requireActivity())
        preferencesService = DILocator.getPreferencesService(super.requireActivity())
        spinnerRole()
        with(binding) {
            firstLoginEtName.requestFocus()
            firstLoginBtCancel.setOnClickListener {
                findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_login)
            }
            firstLoginEtName.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    firstLoginBtNext.callOnClick()
                    firstLoginEtNickname.requestFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            firstLoginEtNickname.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    firstLoginBtNext.callOnClick()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            firstLoginBtNext.setOnClickListener {
                lifecycleScope.launch {
                    if(AndroidUtils.loadIntentWithDestination(this@FirstLoginFragment) { firstLoginBtNext() } == true)
                        super.requireActivity().whenStarted {
                            findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_home)
                        }
                }
            }
            val imagePickerDialog = ImagePickerDialog(firstLoginProfilePicture) {
                coroutineScope {
                    launch { accountViewModel.saveProfilePicture(it) }
                    AndroidComponents.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.account_profile_pic_selected_toast)
                    )
                }
            }
            firstLoginProfilePicture.setOnClickListener {
                AndroidUtils.showDialogFragment(imagePickerDialog, childFragmentManager)
            }
            viewModel.name.observe(viewLifecycleOwner) {
                firstLoginEtName.setText(it)
            }
            viewModel.nickname.observe(viewLifecycleOwner) {
                firstLoginEtNickname.setText(it)
            }
            viewModel.role.observe(viewLifecycleOwner) {
                firstLoginSpinnerRole.setSelection(it.ordinal)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    private suspend fun firstLoginBtNext(): Boolean? = coroutineScope {
        with(binding) {
            when (phase) {
                0 -> {
                    if(!validatorService.validateName(firstLoginEtName, firstLoginEtNameLayout))
                        return@coroutineScope false
                    viewModel.name.value = firstLoginEtName.text.toString()
                    flip()
                    return@coroutineScope false
                }
                1 -> {
                    if(!validatorService.validateNickname(firstLoginEtNickname, firstLoginEtNicknameLayout))
                        return@coroutineScope false
                    viewModel.nickname.value = firstLoginEtNickname.text.toString()
                    flip()
                    return@coroutineScope false
                }
                2 -> {
                    viewModel.role.value = BandItEnums.Account.Role.values()[roleIndex]
                    flip()
                }
                3 -> {
                    launch { createAccount() }.invokeOnCompletion {
                        firstLoginBtNext.setText(R.string.first_login_bt_next_last)
                        firstLoginBtCancel.visibility = View.GONE
                        flip()
                    }

                }
                4 -> return@coroutineScope goToHomePage()
                else -> {}
            }
        }
        return@coroutineScope null
    }

    private fun flip() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.firstLoginBtNext
        )
        binding.firstLoginVfForm.showNext()
        binding.firstLoginProgressBar.progress++
        phase++
    }

    private suspend fun goToHomePage(): Boolean {
        super.requireActivity().viewModelStore.clear()
        viewModel.database.init(viewModel.auth.currentUser!!.uid)
        preferencesService.savePreference(
            Constants.Preferences.REMEMBER_ME,
            this.arguments?.getBoolean(Constants.SafeArgs.REMEMBER_ME) ?: false
        )
        AndroidUtils.unlockNavigation(super.requireActivity())
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.first_login_toast)
        )
        return true
    }

    private suspend fun createAccount() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.firstLoginBtNext
        )
        accountViewModel.createAccount(
            viewModel.name.value!!,
            viewModel.nickname.value!!,
            viewModel.role.value!!
        )
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        roleIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}