package com.bandit.ui.friends

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.FragmentFriendsBinding
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.util.AndroidUtils

class FriendsFragment : Fragment(), OnQueryTextListener {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FriendsViewModel by activityViewModels()
    private val friendsNewDialogFragment = FriendsNewDialogFragment()

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
            friendsBtAddNewFriends.setOnClickListener {
                AndroidUtils.showDialogFragment(
                    friendsNewDialogFragment,
                    childFragmentManager
                )
            }
            friendsBtAdd.setOnClickListener {

            }
            friendsSearchView.setOnQueryTextListener(this@FriendsFragment)
            viewModel.friends.observe(viewLifecycleOwner) {
                friendsList.adapter = PeopleAdapter(it.sorted())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }
}