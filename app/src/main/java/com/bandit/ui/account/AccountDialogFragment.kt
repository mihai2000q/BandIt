package com.bandit.ui.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ImageButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.component.ImagePickerDialog
import com.bandit.constant.BandItEnums
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentAccountBinding
import com.bandit.util.AndroidUtils
import com.bandit.util.PreferencesUtils
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class AccountDialogFragment(private val accountButton: ImageButton) : DialogFragment(), OnItemSelectedListener {

    private var _binding: DialogFragmentAccountBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AccountViewModel by activityViewModels()
    private var roleIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            accountBtSignOut.setOnClickListener { signOut() }
            viewModel.account.observe(viewLifecycleOwner) {
                accountEtName.setText(it.name)
                accountEtNickname.setText(it.nickname)
                roleIndex = it.role.ordinal
            }
            AndroidComponents.spinner(
                super.requireContext(),
                accountSpinnerRole,
                this@AccountDialogFragment,
                BandItEnums.Account.Role.values()
            )
            lifecycleScope.launch {
                val pic = viewModel.getProfilePicture()
                Glide.with(this@AccountDialogFragment)
                    .load(if(pic.isEmpty()) R.drawable.default_avatar else pic)
                    .placeholder(R.drawable.placeholder_profile_pic)
                    .into(accountIvProfilePicture)
            }
            accountBtSave.setOnClickListener {
                AndroidUtils.loadDialogFragment(this@AccountDialogFragment) { updateAccount() }
            }
            val imagePickerDialog = ImagePickerDialog(accountIvProfilePicture) {
                viewModel.updateProfilePicture(it)
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.account_profile_pic_updated_toast)
                )
            }
            accountIvProfilePicture.setOnClickListener {
                AndroidUtils.showDialogFragment(imagePickerDialog, childFragmentManager)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        accountButton.setImageDrawable(
            ContextCompat.getDrawable(
                super.requireContext(),
                R.drawable.ic_account
            )
        )
    }

    private suspend fun updateAccount() {
        with(binding) {
            viewModel.updateAccount(
                accountEtName.text.toString(),
                accountEtNickname.text.toString(),
                BandItEnums.Account.Role.values()[roleIndex]
            )
            AndroidUtils.hideKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, accountBtSave)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.account_updated_toast)
            )
        }
    }

    private fun signOut() {
        viewModel.signOut()
        //go back to login fragment
        val navController = super.requireActivity().findNavController(R.id.main_nav_host)
        for(i in 0 until navController.backQueue.size)
            navController.popBackStack()
        navController.navigate(R.id.navigation_login)

        AndroidUtils.lockNavigation(
            super.requireActivity().findViewById(R.id.main_bottom_navigation_view),
            super.requireActivity().findViewById(R.id.main_drawer_layout)
        )
        PreferencesUtils.resetPreferences(this.requireActivity())
        super.requireActivity().viewModelStore.clear()
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.sign_out_toast),
            Toast.LENGTH_LONG
        )
        super.dismiss()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        roleIndex = position
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        const val TAG = Constants.Account.TAG
    }
}