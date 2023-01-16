package com.bandit.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.bandit.R
import com.bandit.databinding.DialogFragmentHomeAccountBinding
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils

class HomeAccountDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentHomeAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentHomeAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.accountBtSignOut.setOnClickListener {
            DILocator.getAuthenticator().signOut()
            //go back to login fragment
            for(i in 0 until findNavController().backQueue.size)
                findNavController().popBackStack()
            AndroidUtils.lockNavigation(
                super.requireActivity().findViewById(R.id.main_bottom_navigation_view),
                super.requireActivity().findViewById(R.id.main_drawer_layout)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = Constants.Home.ACCOUNT_HOME_TAG
    }

}