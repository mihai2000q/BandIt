package com.bandit.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.FragmentSocialBinding
import com.bandit.ui.adapter.SocialViewPagerAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.friends.FriendsViewModel
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.tabs.TabLayoutMediator

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModel: FriendsViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSocialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            socialHeader.headerTvTitle.text = resources.getString(R.string.title_social)
            AndroidComponents.header(
                super.requireActivity(),
                socialHeader.headerBtAccount
            )
            socialViewPager.adapter = SocialViewPagerAdapter(super.requireActivity())
            TabLayoutMediator(socialTabLayout, socialViewPager) { tab, pos ->
                when(pos) {
                    0 -> tab.text = resources.getString(R.string.social_chats_tab)
                    1 -> {
                        tab.text = resources.getString(R.string.social_friends_tab)
                        friendsViewModel.friendRequests.observe(viewLifecycleOwner) {
                            val badge = tab.orCreateBadge
                            friendsViewModel.friendsTabOpen.observe(viewLifecycleOwner) { value ->
                                badge.isVisible = !value
                            }
                            this@SocialFragment.setTabBadge(badge, it)
                        }
                    }
                    else -> {
                        tab.text = resources.getString(R.string.social_band_tab)
                        bandViewModel.bandInvitations.observe(viewLifecycleOwner) {
                            val badge = tab.orCreateBadge
                            bandViewModel.bandTabOpen.observe(viewLifecycleOwner) { value ->
                                badge.isVisible = !value
                            }
                            this@SocialFragment.setTabBadge(badge, it)
                        }
                    }
                }
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setTabBadge(
        badge: BadgeDrawable,
        list: List<*>
    ) {
        badge.isVisible = list.isNotEmpty()
        badge.number = list.size
        badge.maxCharacterCount = 99
        badge.backgroundColor = ContextCompat.getColor(super.requireContext(), R.color.red)
    }

}