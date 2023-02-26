package com.bandit.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.databinding.ModelBandMemberBinding
import com.bandit.di.DILocator
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

data class BandMemberAdapter(
    private val fragment: Fragment,
    private val members: MutableMap<Account, Boolean>,
    private val viewModel: FriendsViewModel
) : RecyclerView.Adapter<BandMemberAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ModelBandMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelBandMemberBinding.inflate(
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
            memberNickname.text = account.nickname
            memberRole.text = account.role.name
            if(DILocator.getDatabase().currentBand.creator == account.id)
                memberStatus.setText(R.string.band_member_creator)
            else if(hasAccepted) {
                memberStatus.setText(R.string.band_member_accepted_true)
                memberStatus.setTextColor(Color.GREEN)
            }
            else {
                memberStatus.setText(R.string.band_member_accepted_false)
                memberStatus.setTextColor(Color.RED)
            }
            AndroidUtils.setProfilePicture(fragment, viewModel, memberProfilePicture, account.userUid)
        }
    }

}