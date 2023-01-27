package com.bandit.ui.home

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandInvitationBinding
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class BandInvitationDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentBandInvitationBinding? = null
    private val binding get() = _binding!!
    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandInvitationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val bandInvitation = DILocator.database.currentBandInvitation
            bandInvitationTvTitle.text = "You have been invited to ${bandInvitation.band.name}"
            bandInvitationBtAccept.setOnClickListener {
                lifecycleScope.launch {
                    launch { DILocator.database.acceptBandInvitation() }.join()
                    clicked = true
                    super.dismiss()
                }
            }
            bandInvitationBtReject.setOnClickListener {
                super.dismiss()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(clicked) return
        lifecycleScope.launch {
            DILocator.database.rejectBandInvitation()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Home.BAND_INVITATION_TAG
    }

}