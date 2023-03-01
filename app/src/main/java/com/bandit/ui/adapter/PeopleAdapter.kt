package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.databinding.ModelFriendBinding
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

class PeopleAdapter(
    private val fragment: Fragment,
    private val accounts: List<Account>,
    private val viewModel: FriendsViewModel,
    private val onClick: ((Account) -> Unit)? = null
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
            itemView.setOnLongClickListener { onLongClick(holder, account) }
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
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                else -> onUnfriend(holder, account)
            }
        }
    }

    private fun onUnfriend(holder: ViewHolder, account: Account): Boolean {
        AndroidComponents.alertDialog(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.friend_alert_dialog_title),
            holder.binding.root.resources.getString(R.string.friend_alert_dialog_message),
            holder.binding.root.resources.getString(R.string.alert_dialog_positive),
            holder.binding.root.resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(fragment) { viewModel.unfriend(account) }
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