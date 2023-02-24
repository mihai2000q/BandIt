package com.bandit.ui.band

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentCreateBandBinding
import com.bandit.util.AndroidUtils

class BandCreateDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentCreateBandBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentCreateBandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                with(viewModel) {
                    this.name.value = createBandEtName.text.toString()
                    AndroidUtils.loadDialogFragment(this@BandCreateDialogFragment) { this.createBand() }
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

    companion object {
        const val TAG = Constants.Social.Band.CREATE_BAND_TAG
    }

}