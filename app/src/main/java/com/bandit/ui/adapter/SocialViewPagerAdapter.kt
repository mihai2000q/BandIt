package com.bandit.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bandit.constant.Constants
import com.bandit.ui.band.BandFragment
import com.bandit.ui.chats.ChatsFragment
import com.bandit.ui.friends.FriendsFragment

class SocialViewPagerAdapter(fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return Constants.Social.NUMBER_OF_TABS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> ChatsFragment()
            1 -> FriendsFragment()
            else -> BandFragment()
        }
    }
}