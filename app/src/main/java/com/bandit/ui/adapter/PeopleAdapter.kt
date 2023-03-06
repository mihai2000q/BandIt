package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.databinding.ModelFriendBinding
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

class PeopleAdapter(
    private val fragment: Fragment,
    private val accounts: List<Account>,
    private val viewModel: FriendsViewModel,
    private val bandViewModel: BandViewModel? = null,
    private val myAccount: Account? = null,
    private val onClick: ((Account) -> Unit)? = null,
) : RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false
    inner class ViewHolder(val binding: ModelFriendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelFriendBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts[position]

        with(holder) {
            itemView.setOnClickListener { onClick?.invoke(account) }
            itemView.setOnLongClickListener {
                if(bandViewModel != null)
                    onLongClick(holder, account)
                else
                    true
            }
            with(binding) {
                friendName.text = account.name
                friendNickname.text = account.nickname
                friendRole.text = account.printRole()
                AndroidUtils.ifNullHide(friendBandName, account.bandName)
                AndroidUtils.setProfilePicture(fragment, viewModel, friendProfilePicture, account.userUid)
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, account: Account) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.friend_popup_menu)
        if(myAccount?.bandId == null)
            popupMenu.menu.findItem(R.id.friend_popup_menu_add_to_band).isVisible = false
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.friend_popup_menu_add_to_band -> onAddToBand(holder, account)
                else -> onUnfriend(holder, account)
            }
        }
    }

    private fun onAddToBand(holder: ViewHolder, account: Account): Boolean {
        if(bandViewModel?.band?.value!!.members.containsKey(account)) {
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_member_same_band_toast)
            )
            return true
        }
        else if(account.bandId != null) {
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.band_member_in_band_toast)
            )
            return true
        }
        AndroidUtils.loadDialogFragment(bandViewModel.viewModelScope, fragment) {
            bandViewModel.sendBandInvitation(account)
        }
        AndroidComponents.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.band_invitation_sent_toast),
        )
        return true
    }

    private fun onUnfriend(holder: ViewHolder, account: Account): Boolean {
        AndroidComponents.alertDialog(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.friend_alert_dialog_title),
            holder.binding.root.resources.getString(R.string.friend_alert_dialog_message),
            holder.binding.root.resources.getString(R.string.alert_dialog_positive),
            holder.binding.root.resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                fragment) { viewModel.unfriend(account) }
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.friend_unfriended_toast),
            )
        }
        return true
    }

    private fun onLongClick(holder: ViewHolder, account: Account): Boolean {
        if(!isPopupShown) {
            popupMenu(holder, account)
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

}