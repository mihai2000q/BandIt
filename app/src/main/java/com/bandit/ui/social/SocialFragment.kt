package com.bandit.ui.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bandit.R
import com.bandit.databinding.FragmentSocialBinding
import com.bandit.ui.adapter.SocialViewPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class SocialFragment : Fragment() {

    private var _binding: FragmentSocialBinding? = null
    private val binding get() = _binding!!

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
            socialViewPager.adapter = SocialViewPagerAdapter(super.requireActivity())
            TabLayoutMediator(socialTabLayout, socialViewPager) { tab, pos ->
                when(pos) {
                    0 -> tab.text = resources.getString(R.string.social_chats_tab)
                    else -> tab.text = resources.getString(R.string.social_friends_tab)
                }
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}