package com.bandit.ui.friends

import android.app.ActionBar
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.databinding.DialogFragmentFriendsBinding
import com.bandit.ui.adapter.FriendRequestAdapter
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.helper.TouchHelper
import com.bandit.util.AndroidUtils

class FriendsRequestsDialogFragment : DialogFragment(), OnQueryTextListener {
    private var _binding: DialogFragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentFriendsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            friendsDialogTvTitle.setText(R.string.friends_requests_dialog_title)
            friendsDialogTvEmpty.setText(R.string.friends_requests_rv_empty)
            friendsDialogSearchView.setOnQueryTextListener(this@FriendsRequestsDialogFragment)
            val touchHelper = TouchHelper<Account>(
                super.requireContext(),
                friendsDialogRvList,
                { req -> onRejectFriendRequest(req) },
                { req -> onAcceptFriendRequest(req) },
                R.drawable.ic_check_circle_outline_white,
                R.drawable.ic_remove_circle_outline_white
            )
            ItemTouchHelper(touchHelper).attachToRecyclerView(friendsDialogRvList)
            viewModel.friendRequests.observe(viewLifecycleOwner) {
                if(it.isNotEmpty()) {
                    friendsDialogRvList.visibility = View.VISIBLE
                    friendsDialogRvEmpty.visibility = View.GONE
                    touchHelper.updateItems(it.sorted())
                    friendsDialogRvList.adapter = FriendRequestAdapter(
                        this@FriendsRequestsDialogFragment,
                        it.sorted(),
                        viewModel,
                        { req -> onAcceptFriendRequest(req) },
                        { req -> onRejectFriendRequest(req) }
                    )
                }
                else {
                    friendsDialogRvList.visibility = View.GONE
                    friendsDialogRvEmpty.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        binding.friendsDialogSearchView.setQuery("", false)
    }

    private fun onAcceptFriendRequest(account: Account) {
        AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
            viewModel.acceptFriendRequest(account)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.friend_request_accepted_toast)
            )
            super.dismiss()
        }
    }

    private fun onRejectFriendRequest(account: Account) {
        AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
            viewModel.rejectFriendRequest(account)
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.friend_request_rejected_toast)
            )
            super.dismiss()
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.friend_request_filtered_toast)
        )
        binding.friendsDialogSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterFriendRequests(newText)
        viewModel.friendRequestsFilterName.value = newText
        return false
    }

    companion object {
        const val TAG = Constants.Social.Friends.ADD_TAG
    }
}