package com.bandit.ui.band

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandInvitationBinding
import com.bandit.di.DILocator
import kotlinx.coroutines.launch

class BandInvitationDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentBandInvitationBinding? = null
    private val binding get() = _binding!!
    private val bandViewModel: BandViewModel by activityViewModels()
    private val _database = DILocator.database
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
            val bandInvitation = _database.currentBandInvitation
            bandInvitationTvTitle.text = buildString {
                append("You have been invited to ")
                append(bandInvitation.band.name)
            }
            bandInvitationBtAccept.setOnClickListener {
                lifecycleScope.launch {
                    launch { _database.acceptBandInvitation() }.join()
                    bandViewModel.refresh()
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
            _database.rejectBandInvitation()
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