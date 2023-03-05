package com.bandit.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentFriendsBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.ui.band.BandViewModel
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
            viewModel.friendsTabOpen.value = true
            val badgeDrawable = BadgeDrawable.create(super.requireContext())
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                friendsBtRequests,
                viewModel.friendRequests.value?.size ?: 0,
                viewModel.friendRequests.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            viewModel.friendRequests.observe(viewLifecycleOwner) {
                badgeDrawable.isVisible = it.isNotEmpty()
                badgeDrawable.number = it.size
            }
            friendsBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    friendsAddDialogFragment,
                    childFragmentManager
                )
            }
            friendsBtRequests.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    friendsRequestsDialogFragment,
                    childFragmentManager
                )
                badgeDrawable.isVisible = false
            }
            friendsSearchView.setOnQueryTextListener(this@FriendsFragment)
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.friends,
                friendsRvList,
                friendsRvEmpty
            ) {
                return@setRecyclerViewEmpty PeopleAdapter(
                    this@FriendsFragment,
                    it.sorted(),
                    viewModel,
                    bandViewModel,
                    accountViewModel.account.value!!
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
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