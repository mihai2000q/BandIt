package com.bandit.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Account
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentFriendsBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.helper.TouchHelper
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class FriendsFragment : Fragment(), OnQueryTextListener {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()
    private val friendsAddDialogFragment = FriendsAddDialogFragment()
    private val friendsRequestsDialogFragment = FriendsRequestsDialogFragment()
    private val friendsDetailDialogFragment = FriendsDetailDialogFragment()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidUtils.setupRefreshLayout(this@FriendsFragment, friendsRvList)
            viewModel.friendsTabOpen.value = true
            val badgeDrawable = BadgeDrawable.create(super.requireContext())
            val badgeDrawable2 = BadgeDrawable.create(super.requireContext())
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                friendsBtRequests,
                viewModel.friendRequests.value?.size ?: 0,
                viewModel.friendRequests.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable2,
                friendsBtOptions,
                viewModel.friendRequests.value?.size ?: 0,
                viewModel.friendRequests.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            viewModel.friendRequests.observe(viewLifecycleOwner) {
                badgeDrawable.isVisible = it.isNotEmpty()
                badgeDrawable.number = it.size
                badgeDrawable2.isVisible = it.isNotEmpty()
                badgeDrawable2.number = it.size
            }
            friendsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    friendsAddDialogFragment,
                    childFragmentManager
                )
                friendsBtOptions.performClick()
            }
            friendsBtRequests.setOnClickListener {
                badgeDrawable.isVisible = false
                badgeDrawable2.isVisible = false
                AndroidUtils.showDialogFragment(
                    friendsRequestsDialogFragment,
                    childFragmentManager
                )
                friendsBtOptions.performClick()
            }
            friendsSearchView.setOnQueryTextListener(this@FriendsFragment)
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.friends,
                friendsRvList,
                friendsRvEmpty
            ) {
                ItemTouchHelper(object : TouchHelper<Account>(
                    super.requireContext(),
                    friendsRvList,
                    { account -> onUnfriend(account) },
                    { account -> onAddToBand(account) }
                ) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        items = it.sorted()
                        super.onSwiped(viewHolder, direction)
                    }
                }).attachToRecyclerView(friendsRvList)
                return@setRecyclerViewEmpty PeopleAdapter(
                    this@FriendsFragment,
                    it.sorted(),
                    viewModel,
                    { acc -> onAddToBand(acc) },
                    { acc -> onUnfriend(acc) },
                    bandViewModel,
                    accountViewModel.account.value!!,
                    onClick = { acc ->
                        viewModel.selectedFriend.value = acc
                        AndroidUtils.showDialogFragment(
                            friendsDetailDialogFragment,
                            childFragmentManager
                        )
                    }
                )
            }
            AndroidUtils.setupFabOptions(
                this@FriendsFragment,
                friendsRvList,
                friendsBtOptions,
                friendsBtAdd,
                friendsBtRequests
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun onAddToBand(account: Account) {
        if(bandViewModel.band.value!!.members.containsKey(account)) {
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_member_same_band_toast)
            )
            return
        }
        else if(account.bandId != null) {
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_member_in_band_toast)
            )
            return
        }
        AndroidUtils.loadDialogFragment(bandViewModel.viewModelScope, this) {
            bandViewModel.sendBandInvitation(account)
        }
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.band_invitation_sent_toast),
        )
        return
    }

    private fun onUnfriend(account: Account) {
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.friend_alert_dialog_title),
            resources.getString(R.string.friend_alert_dialog_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                this) { viewModel.unfriend(account) }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.friend_unfriended_toast),
            )
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.friend_filtered_toast)
        )
        binding.friendsSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterFriends(newText)
        viewModel.friendsFilterName.value = newText
        return false
    }
}