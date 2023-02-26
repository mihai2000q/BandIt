package com.bandit.ui.friends

import android.app.ActionBar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentFriendsBinding
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.util.AndroidUtils

class FriendsAddDialogFragment : DialogFragment(), OnQueryTextListener {
    private var _binding: DialogFragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentFriendsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            viewModel.people.observe(viewLifecycleOwner) {
                friendsDialogList.adapter = PeopleAdapter(
                    this@FriendsAddDialogFragment,
                    it.sorted(),
                    viewModel
                )
                { acc ->
                    AndroidUtils.loadDialogFragment(this@FriendsAddDialogFragment) {
                        viewModel.sendFriendRequest(acc)
                    }
                    AndroidComponents.toastNotification(
                        super.requireContext(),
                        resources.getString(R.string.friend_request_sent_toast)
                    )
                    super.dismiss()
                }
            }
            friendsDialogSearchView.setOnQueryTextListener(this@FriendsAddDialogFragment)
            friendsDialogTitle.setText(R.string.friends_add_dialog_title)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Social.Friends.NEW_FRIEND_TAG
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        AndroidComponents.toastNotification(
            super.requireContext(),
            resources.getString(R.string.people_filtered_toast)
        )
        binding.friendsDialogSearchView.clearFocus()
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.filterPeople(newText)
        return false
    }
}