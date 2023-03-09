package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.BandInvitation
import com.bandit.databinding.ModelBandInvitationBinding
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils

class BandInvitationAdapter(
    private val bandInvitations: List<BandInvitation>,
    private val onAcceptBandInvitation: (BandInvitation) -> Unit,
    private val onRejectBandInvitation: (BandInvitation) -> Unit
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
            bandInvitationTvBandName.text = bandInvitation.band.name
            bandInvitationBtAccept.setOnClickListener { onAcceptBandInvitation(bandInvitation) }
            bandInvitationBtReject.setOnClickListener { onRejectBandInvitation(bandInvitation) }
        }
    }
}