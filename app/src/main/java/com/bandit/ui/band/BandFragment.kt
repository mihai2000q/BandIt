package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.databinding.FragmentBandBinding
import com.bandit.ui.account.AccountViewModel
import com.bandit.ui.adapter.BandMemberAdapter
import com.bandit.ui.component.AndroidComponents
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
            AndroidUtils.setupRefreshLayout(this@BandFragment, bandRvMemberList)
            val bandAddMemberDialogFragment = BandAddMemberDialogFragment()
            val bandCreateBandDialogFragment = BandCreateDialogFragment()
            val bandInvitationDialogFragment = BandInvitationDialogFragment()
            viewModel.bandTabOpen.value = true
            val badgeDrawable = BadgeDrawable.create(super.requireContext())
            val badgeDrawable2 = BadgeDrawable.create(super.requireContext())
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable,
                bandBtInvitations,
                viewModel.bandInvitations.value?.size ?: 0,
                viewModel.bandInvitations.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            AndroidUtils.setBadgeDrawableOnView(
                badgeDrawable2,
                bandBtOptions,
                viewModel.bandInvitations.value?.size ?: 0,
                viewModel.bandInvitations.value?.isNotEmpty() ?: false,
                ContextCompat.getColor(super.requireContext(), R.color.red)
            )
            AndroidUtils.setupFabScrollUp(
                super.requireContext(),
                bandRvMemberList,
                bandBtScrollUp
            )
            viewModel.bandInvitations.observe(viewLifecycleOwner) {
                if(viewModel.badgePreviousSize == badgeDrawable.number) return@observe
                badgeDrawable.isVisible = it.isNotEmpty()
                badgeDrawable.number = it.size
                badgeDrawable2.isVisible = it.isNotEmpty()
                badgeDrawable2.number = it.size
            }
            viewModel.band.observe(viewLifecycleOwner) {
                if(viewModel.band.value!!.creator == accountViewModel.account.value!!.id) {
                    bandBtAbandon.tooltipText = resources.getString(R.string.content_description_bt_disband_band)
                    bandBtAbandon.contentDescription = resources.getString(R.string.content_description_bt_disband_band)
                    bandFabTvAbandon.text = resources.getString(R.string.band_fab_disband)
                    bandBtAbandon.setOnClickListener { this@BandFragment.onDisband() }
                }
                else {
                    bandBtAbandon.tooltipText = resources.getString(R.string.content_description_bt_abandon_band)
                    bandBtAbandon.contentDescription = resources.getString(R.string.content_description_bt_abandon_band)
                    bandFabTvAbandon.text = resources.getString(R.string.band_fab_abandon)
                    bandBtAbandon.setOnClickListener { this@BandFragment.onAbandon() }
                }
                bandTvName.text = viewModel.band.value?.name
                if(it.isEmpty()) {
                    bandTvName.visibility = View.GONE
                    bandRvMemberList.visibility = View.GONE
                    bandSearchView.visibility = View.INVISIBLE
                    layoutBandEmpty.visibility = View.VISIBLE
                    AndroidUtils.setupFabOptions(
                        this@BandFragment,
                        bandRvMemberList,
                        bandBtOptions,
                        listOf(bandBtInvitations),
                        listOf(bandFabTvInvitations)
                    )
                    bandBtAdd.visibility = View.GONE
                }
                else {
                    bandTvName.visibility = View.VISIBLE
                    bandRvMemberList.visibility = View.VISIBLE
                    bandSearchView.visibility = View.VISIBLE
                    layoutBandEmpty.visibility = View.GONE
                    bandBtAdd.visibility = View.INVISIBLE
                    AndroidUtils.setupFabOptions(
                        this@BandFragment,
                        bandRvMemberList,
                        bandBtOptions,
                        listOf(bandBtAdd, bandBtInvitations, bandBtAbandon),
                        listOf(bandFabTvAdd, bandFabTvInvitations, bandFabTvAbandon)
                    )
                }
            }
            viewModel.members.observe(viewLifecycleOwner) {
                bandRvMemberList.adapter = BandMemberAdapter(
                    this@BandFragment, it, viewModel,
                    friendsViewModel, accountViewModel.account.value!!
                )
            }
            bandBtAdd.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    bandAddMemberDialogFragment,
                    childFragmentManager
                )
                bandBtOptions.performClick()
            }
            bandBtInvitations.setOnClickListener {
                badgeDrawable.isVisible = false
                badgeDrawable2.isVisible = false
                viewModel.badgePreviousSize = badgeDrawable.number
                AndroidUtils.showDialogFragment(
                    bandInvitationDialogFragment,
                    childFragmentManager
                )
                bandBtOptions.performClick()
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

    private fun onAbandon() {
        binding.bandBtOptions.performClick()
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.band_alert_dialog_abandon_title),
            resources.getString(R.string.band_alert_dialog_abandon_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                this) { viewModel.abandonBand() }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_abandoned_toast),
            )
        }
    }

    private fun onDisband() {
        binding.bandBtOptions.performClick()
        AndroidComponents.alertDialog(
            super.requireContext(),
            resources.getString(R.string.band_alert_dialog_disband_title),
            resources.getString(R.string.band_alert_dialog_disband_message),
            resources.getString(R.string.alert_dialog_positive),
            resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
                viewModel.disbandBand()
                viewModelStore.clear()
            }
            AndroidComponents.toastNotification(
                super.requireContext(),
                resources.getString(R.string.band_disbanded_toast),
            )
        }
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
        viewModel.filterMembersName.value = newText
        return true
    }
}