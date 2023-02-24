package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.data.model.Account
import com.bandit.databinding.DialogFragmentBandAddMemberBinding
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.ui.friends.FriendsViewModel
import com.bandit.util.AndroidUtils

class BandAddMemberDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentBandAddMemberBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BandViewModel by activityViewModels()
    private val friendsViewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandAddMemberBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            friendsViewModel.friends.observe(viewLifecycleOwner) {
                val accounts = it.sorted() - (viewModel.members.value?.keys as Set<Account>)
                bandAddMemberFriends.adapter = PeopleAdapter(
                    this@BandAddMemberDialogFragment,
                    accounts.filter { acc -> acc.bandId == null },
                    friendsViewModel
                ) { acc ->
                    AndroidUtils.loadDialogFragment(this@BandAddMemberDialogFragment) {
                        viewModel.sendBandInvitation(acc)
                    }
                    AndroidComponents.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.band_invitation_sent_toast)
                    )
                    super.dismiss()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Social.Band.ADD_MEMBER_TAG
    }
}