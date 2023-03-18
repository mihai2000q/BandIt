package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandMemberDetailBinding
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

class BandMemberDetailDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentBandMemberDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandMemberDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            AndroidUtils.getScreenHeight(super.requireActivity()) / 2
        )
        with(binding) {
            viewModel.selectedBandMember.observe(viewLifecycleOwner) {
                AndroidUtils.setProfilePicture(
                    this@BandMemberDetailDialogFragment,
                    friendsViewModel,
                    bandMemberDetailProfilePic,
                    it.userUid
                )
                bandMemberDetailTvEmail.text = it.email
                bandMemberDetailTvBandName.text = if(it.bandName.isNullOrBlank()) "" else it.bandName
                bandMemberDetailTvName.text = it.name
                bandMemberDetailTvNickname.text = it.nickname
                bandMemberDetailTvRole.text = it.printRole()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Social.Band.DETAIL_MEMBER_TAG
    }
}
