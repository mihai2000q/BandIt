package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.BandInvitation
import com.bandit.databinding.ModelBandInvitationBinding
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils

class BandInvitationAdapter(
    private val dialogFragment: DialogFragment,
    private val bandInvitations: List<BandInvitation>,
    private val viewModel: BandViewModel
) : RecyclerView.Adapter<BandInvitationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ModelBandInvitationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelBandInvitationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return bandInvitations.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bandInvitation = bandInvitations[position]

        with(holder.binding) {
            bandInvitationBandName.text = bandInvitation.band.name
            bandInvitationBtAccept.setOnClickListener { onAccept(holder, bandInvitation) }
            bandInvitationBtReject.setOnClickListener { onReject(holder, bandInvitation) }
        }
    }

    private fun onAccept(holder: ViewHolder, bandInvitation: BandInvitation) {
        AndroidUtils.loadDialogFragment(dialogFragment) {
            viewModel.acceptBandInvitation(bandInvitation)
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_invitation_accepted_toast)
            )
            dialogFragment.dismiss()
        }
    }

    private fun onReject(holder: ViewHolder, bandInvitation: BandInvitation) {
        AndroidUtils.loadDialogFragment(dialogFragment) {
            viewModel.rejectBandInvitation(bandInvitation)
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_invitation_rejected_toast)
            )
            dialogFragment.dismiss()
        }
    }
}