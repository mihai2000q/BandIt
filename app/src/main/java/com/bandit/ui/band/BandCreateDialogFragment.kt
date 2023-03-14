package com.bandit.ui.band

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentCreateBandBinding
import com.bandit.di.DILocator
import com.bandit.service.IValidatorService
import com.bandit.ui.account.AccountViewModel
import com.bandit.util.AndroidUtils

class BandCreateDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentCreateBandBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()
    private lateinit var validatorService: IValidatorService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateBandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        validatorService = DILocator.getValidatorService(super.requireActivity())
        with(binding) {
            viewModel.name.observe(viewLifecycleOwner) {
                createBandEtName.setText(it)
            }
            createBandEtName.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    createBandBtCreate.callOnClick()
                    createBandBtCreate.requestFocus()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            createBandBtCreate.setOnClickListener {
                if(!validateFields()) return@setOnClickListener
                with(viewModel) {
                    this.name.value = createBandEtName.text.toString()
                    AndroidUtils.loadDialogFragment(
                        this.viewModelScope,
                        this@BandCreateDialogFragment
                    ) {
                        this.createBand(accountViewModel.account.value!!.id)
                    }
                    AndroidComponents.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.band_create_toast)
                    )
                }
                super.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateFields(): Boolean {
        return validatorService.validateName(binding.createBandEtName, binding.createBandEtNameLayout)
    }

    companion object {
        const val TAG = Constants.Social.Band.CREATE_BAND_TAG
    }

}