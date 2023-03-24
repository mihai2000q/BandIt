package com.bandit.ui.component

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBottomSheetBinding
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TypingBottomSheetDialogFragment(
    private val event: (EditText) -> Unit
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
            bottomSheetDfEditText.requestFocus()
            bottomSheetDfEditText.doOnTextChanged { text, _, _, _ ->
                val outAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
                val inAnim = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
                if(text!!.isNotEmpty()) {
                    bottomSheetDfBtSend.apply {
                        if(visibility == View.VISIBLE) return@doOnTextChanged
                        startAnimation(inAnim)
                        postOnAnimation {
                            visibility = View.VISIBLE
                        }
                    }
                } else {
                    bottomSheetDfBtSend.apply {
                        if(visibility == View.GONE) return@doOnTextChanged
                        startAnimation(outAnim)
                        postOnAnimation {
                            visibility = View.GONE
                        }
                    }
                }
            }
            AndroidUtils.showKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, bottomSheetDfEditText)
            bottomSheetDfEditText.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    bottomSheetDfBtSend.performClick()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            bottomSheetDfBtSend.setOnClickListener {
                if(!bottomSheetDfEditText.text.isNullOrBlank()) {
                    this@TypingBottomSheetDialogFragment.event.invoke(bottomSheetDfEditText)
                    binding.bottomSheetDfEditText.setText("")
                }
                super.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Component.BOTTOM_SHEET_DIALOG_FRAGMENT_TAG
    }
}