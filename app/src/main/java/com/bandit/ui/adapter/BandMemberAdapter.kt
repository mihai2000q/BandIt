package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.databinding.ModelBandMemberBinding
import com.bandit.ui.band.BandMemberDetailDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

data class BandMemberAdapter(
    private val fragment: Fragment,
    private val members: MutableMap<Account, Boolean>,
    private val viewModel: BandViewModel,
    private val friendsViewModel: FriendsViewModel,
    private val myAccount: Account
) : RecyclerView.Adapter<BandMemberAdapter.ViewHolder>() {
    private val bandMemberDetailDialogFragment = BandMemberDetailDialogFragment()
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false
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
        with(holder) {
            itemView.setOnClickListener { onClick(account) }
            itemView.setOnLongClickListener { onLongClick(holder, account) }
            with(binding) {
                AndroidUtils.setProfilePicture(fragment, friendsViewModel,
                    memberProfilePicture, account.userUid)
                memberNickname.text = account.nickname
                memberRole.text = account.printRole()
                if(viewModel.band.value!!.creator == account.id)
                    memberStatus.setText(R.string.band_member_creator)
                else if(hasAccepted) {
                    memberStatus.setText(R.string.band_member_accepted_true)
                    memberStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.band_member_accepted
                        )
                    )
                }
                else {
                    memberStatus.setText(R.string.band_member_accepted_false)
                    memberStatus.setTextColor(
                        ContextCompat.getColor(
                            holder.binding.root.context,
                            R.color.band_member_pending
                        )
                    )
                }
                if(account == myAccount) {
                    bandMemberCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            binding.root.context,
                            R.color.band_member_my_account_color
                        )
                    )
                    memberRole.visibility = View.GONE
                    memberNickname.text = holder.binding.root.resources.getString(R.string.band_member_you)
                }
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, account: Account) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.band_member_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                else -> onKick(holder, account)
            }
        }
    }

    private fun onKick(holder: ViewHolder, account: Account): Boolean {
        if(viewModel.band.value!!.creator == account.id) {
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_member_tried_kick_toast)
            )
            return true
        }
        AndroidComponents.alertDialog(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.band_alert_dialog_kick_title),
            holder.binding.root.resources.getString(R.string.band_alert_dialog_kick_message),
            holder.binding.root.resources.getString(R.string.alert_dialog_positive),
            holder.binding.root.resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope, fragment) {
                viewModel.kickBandMember(account)
                friendsViewModel.removeBandForFriend(account)
            }
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_member_kicked_toast),
            )
        }
        return true
    }

    private fun onLongClick(holder: ViewHolder, account: Account): Boolean {
        if(!isPopupShown && account != myAccount) {
            popupMenu(holder, account)
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

    private fun onClick(account: Account) {
        viewModel.selectedBandMember.value = account
        AndroidUtils.showDialogFragment(
            bandMemberDetailDialogFragment,
            fragment.childFragmentManager
        )
    }

}