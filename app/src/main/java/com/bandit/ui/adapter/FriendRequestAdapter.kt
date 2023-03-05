package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.Account
import com.bandit.databinding.ModelFriendRequestBinding
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

class FriendRequestAdapter(
    private val fragment: DialogFragment,
    private val friendRequests: List<Account>,
    private val viewModel: FriendsViewModel
) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ModelFriendRequestBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelFriendRequestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return friendRequests.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friendRequest = friendRequests[position]

        with(holder.binding) {
            friendRqName.text = friendRequest.name
            friendRqNickname.text = friendRequest.nickname
            friendRqBtAccept.setOnClickListener { accept(holder, friendRequest) }
            friendRqBtReject.setOnClickListener { reject(holder, friendRequest) }
            AndroidUtils.setProfilePicture(fragment, viewModel, friendRqProfilePicture, friendRequest.userUid)
        }
    }

    private fun accept(holder: ViewHolder, friendRequest: Account) {
        AndroidUtils.loadDialogFragment(fragment) {
            viewModel.acceptFriendRequest(friendRequest)
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.friend_request_accepted_toast)
            )
            fragment.dismiss()
        }
    }

    private fun reject(holder: ViewHolder, friendRequest: Account) {
        AndroidUtils.loadDialogFragment(fragment) {
            viewModel.rejectFriendRequest(friendRequest)
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.friend_request_rejected_toast)
            )
            fragment.dismiss()
        }
    }
}