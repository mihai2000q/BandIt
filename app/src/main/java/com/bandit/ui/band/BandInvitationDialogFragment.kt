package com.bandit.ui.band

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandInvitationBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils

class BandInvitationDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentBandInvitationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()
    private var clicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandInvitationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val bandInvitation = DILocator.getDatabase().currentBandInvitation
            bandInvitationTvTitle.text = buildString {
                append("You have been invited to ")
                append(bandInvitation.band.name)
            }
            bandInvitationBtAccept.setOnClickListener {
                viewModel.acceptBandInvitation()
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.band_invite_accepted_toast)
                )
                clicked = true
                super.dismiss()
            }
            bandInvitationBtReject.setOnClickListener {
                super.dismiss()
            }
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(clicked) return
        viewModel.rejectBandInvitation()
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.band_invite_rejected_toast)
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Home.BAND_INVITATION_TAG
    }

}