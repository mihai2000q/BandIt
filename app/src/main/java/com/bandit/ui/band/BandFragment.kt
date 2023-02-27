package com.bandit.ui.band

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
import com.bandit.databinding.FragmentBandBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.BandMemberAdapter
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils
import com.google.android.material.badge.BadgeDrawable

class BandFragment : Fragment(), OnQueryTextListener {
    private var _binding: FragmentBandBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBandBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val bandAddMemberDialogFragment = BandAddMemberDialogFragment()
            val bandCreateBandDialogFragment = BandCreateDialogFragment()
            val bandInvitationDialogFragment = BandInvitationDialogFragment()
            viewModel.bandTabOpen.value = true
            val badgeDrawable = BadgeDrawable.create(super.requireContext())
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                bandBtInvitations,
                viewModel.bandInvitations.value?.size ?: 0,
                viewModel.bandInvitations.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            viewModel.bandInvitations.observe(viewLifecycleOwner) {
                badgeDrawable.isVisible = it.isNotEmpty()
                badgeDrawable.number = it.size
            }
            viewModel.band.observe(viewLifecycleOwner) {
                bandTvName.text = viewModel.band.value?.name
                if(it.isEmpty()) {
                    bandTvName.visibility = View.GONE
                    bandRvMemberList.visibility = View.GONE
                    bandBtCreate.visibility = View.VISIBLE
                    bandSearchView.visibility = View.INVISIBLE
                    bandBtAdd.visibility = View.INVISIBLE
                    layoutBandEmpty.visibility = View.VISIBLE
                }
                else {
                    bandTvName.visibility = View.VISIBLE
                    bandBtCreate.visibility = View.GONE
                    bandRvMemberList.visibility = View.VISIBLE
                    bandSearchView.visibility = View.VISIBLE
                    bandBtAdd.visibility = View.VISIBLE
                    layoutBandEmpty.visibility = View.GONE
                }
            }
            viewModel.members.observe(viewLifecycleOwner) {
                bandRvMemberList.adapter = BandMemberAdapter(
                    this@BandFragment, it, friendsViewModel, accountViewModel.account.value!!
                )
            }
            bandBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandAddMemberDialogFragment,
                    childFragmentManager
                )
            }
            bandBtInvitations.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandInvitationDialogFragment,
                    childFragmentManager
                )
                badgeDrawable.isVisible = false
            }
            bandBtCreate.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandCreateBandDialogFragment,
                    childFragmentManager
                )
            }
            bandSearchView.setOnQueryTextListener(this@BandFragment)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.band_members_filtered_toast)
        )
        binding.bandSearchView.clearFocus()
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterBandMembers(name = newText)
        return true
    }
}