package com.bandit.ui.first.login

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
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
    private var phase = 0
    private var roleIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        spinnerRole()
        with(binding) {
            firstLoginSpinnerRole.visibility = View.GONE
            firstLoginBtCancel.setOnClickListener {
                findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_login)
            }
            firstLoginBtNext.setOnClickListener { firstLoginBtNext() }
            // TODO: on last phase it stays disabled
            /*firstLoginEtString.addTextChangedListener {
                firstLoginBtNext.isEnabled = it.toString().isNotEmpty()
            }*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun firstLoginBtNext() {
        with(binding) {
            when (phase) {
                0 -> {
                    viewModel.name.value = firstLoginEtString.text.toString()
                    phase(resources.getString(R.string.first_login_tv_subject_nickname))
                }
                1 -> {
                    viewModel.nickname.value = firstLoginEtString.text.toString()
                    phase(resources.getString(R.string.first_login_tv_subject_role))
                    firstLoginEtString.visibility = View.INVISIBLE
                    firstLoginSpinnerRole.visibility = View.VISIBLE
                    AndroidUtils.hideKeyboard(
                        super.requireActivity(),
                        Context.INPUT_METHOD_SERVICE,
                        binding.firstLoginTitle
                    )
                }
                2 -> {
                    viewModel.role.value = BandItEnums.Account.Role.values()[roleIndex]
                    firstLoginSpinnerRole.visibility = View.GONE
                    firstLoginBtCancel.visibility = View.GONE
                    firstLoginBtNext.setText(R.string.first_login_bt_next_last)
                    phase(resources.getString(R.string.first_login_tv_subject_last))
                    createAccount()
                }
                3 -> {
                    lifecycleScope.launch {
                        DILocator.database.init()
                    }
                    PreferencesUtils.savePreference(
                        super.requireActivity(),
                        Constants.Preferences.REMEMBER_ME,
                        viewModel.rememberMe.value ?: false
                    )
                    AndroidUtils.unlockNavigation(
                        super.requireActivity().findViewById(R.id.main_bottom_navigation_view),
                        super.requireActivity().findViewById(R.id.main_drawer_layout)
                    )
                    super.requireActivity().findViewById<TextView>(R.id.header_tv_email).text =
                        DILocator.database.currentAccount.email
                    super.requireActivity().findViewById<TextView>(R.id.header_tv_name).text =
                        DILocator.database.currentAccount.name
                    findNavController().navigate(R.id.action_firstLoginFragment_to_navigation_home)
                }
                else -> {}
            }
        }
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

    private fun phase(tvSubjectText: String) {
        with(binding) {
            firstLoginEtString.setText("")
            firstLoginTvSubject.text = tvSubjectText
            firstLoginProgressBar.progress++
            phase++
        }
    }

    private fun createAccount() {
        AndroidUtils.hideKeyboard(
            super.requireActivity(),
            Context.INPUT_METHOD_SERVICE,
            binding.firstLoginTitle
        )
        viewModel.createAccount()
        lifecycleScope.launch {
            DILocator.database.setUserAccountSetup(true)
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        roleIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

}