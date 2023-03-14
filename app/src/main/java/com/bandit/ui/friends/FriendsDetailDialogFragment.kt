package com.bandit.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentFriendDetailBinding
import com.bandit.util.AndroidUtils

class FriendsDetailDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentFriendDetailBinding? = null
    private val binding get() = _binding!!
    private val friendsViewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentFriendDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            AndroidUtils.getScreenHeight(super.requireActivity()) * 3 / 4
        )
        with(binding) {
            friendsViewModel.selectedFriend.observe(viewLifecycleOwner) {
                AndroidUtils.setProfilePicture(
                    this@FriendsDetailDialogFragment,
                    friendsViewModel,
                    friendDetailProfilePic,
                    it.userUid
                )
                friendDetailTvEmail.text = it.email
                friendDetailTvBandName.text = if(it.bandName.isNullOrBlank()) "" else it.bandName
                friendDetailTvName.text = it.name
                friendDetailTvNickname.text = it.nickname
                friendDetailTvRole.text = it.printRole()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Social.Friends.DETAIL_TAG
    }
}