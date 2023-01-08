package com.bandit.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bandit.R
import com.bandit.databinding.DialogFragmentHomeAccountBinding
import com.bandit.helper.Constants

class HomeAccountDialogFragment : DialogFragment() {

    private var binding: DialogFragmentHomeAccountBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogFragmentHomeAccountBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = Constants.Home.ACCOUNT_HOME_TAG
    }

}