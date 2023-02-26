package com.bandit.ui.component

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBottomSheetBinding
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TypingBottomSheetDialogFragment(
    private val onDismissEvent: (EditText) -> Unit
) : BottomSheetDialogFragment() {
    private var _binding: DialogFragmentBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidUtils.showKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, bottomSheetDfEditText)
            bottomSheetDfEditText.requestFocus()
            bottomSheetDfEditText.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    super.dismiss()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(validateField())
            onDismissEvent(binding.bottomSheetDfEditText)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun validateField(): Boolean {
        if(binding.bottomSheetDfEditText.text.isNullOrBlank()) {
            return false
        }
        return true
    }

    companion object {
        const val TAG = Constants.Component.BOTTOM_SHEET_DIALOG_FRAGMENT_TAG
    }
}