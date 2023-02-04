package com.bandit.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.databinding.ModelMemberBinding
import com.bandit.di.DILocator

data class BandAdapter(
    private val members: MutableMap<Account, Boolean>
) : RecyclerView.Adapter<BandAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ModelMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelMemberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return members.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = members.keys.toList()[position]
        val hasAccepted = members.values.toList()[position]
        with(holder.binding) {
            bandTvName.text = account.name
            bandTvRole.text = account.role.name
            if(DILocator.database.currentBand.creator == account.id)
                bandTvAccepted.setText(R.string.band_member_creator)
            else if(hasAccepted) {
                bandTvAccepted.setText(R.string.band_member_accepted_true)
                bandTvAccepted.setTextColor(Color.GREEN)
            }
            else {
                bandTvAccepted.setText(R.string.band_member_accepted_false)
                bandTvAccepted.setTextColor(Color.RED)
            }
        }
    }

}