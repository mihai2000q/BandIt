package com.bandit.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.HeaderFragmentBinding
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.BandViewModel
import com.bandit.ui.band.CreateBandDialogFragment
import com.bandit.util.AndroidUtils

open class BaseFragment : Fragment() {

    private var _headerBinding: HeaderFragmentBinding? = null
    private val headerBinding get() = _headerBinding!!
    private val bandViewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _headerBinding = HeaderFragmentBinding.inflate(layoutInflater, container, false)
        return headerBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val accountDialogFragment = AccountDialogFragment(headerBinding.headerBtAccount)
        val createBandDialogFragment = CreateBandDialogFragment()
        val bandDialogFragment = BandDialogFragment()
        with(headerBinding) {
            AndroidUtils.accountButton(
                super.requireActivity(),
                headerBtAccount,
                accountDialogFragment
            )
            AndroidUtils.bandButton(
                super.requireActivity(),
                headerBtBand,
                bandViewModel.band,
                viewLifecycleOwner,
                createBandDialogFragment,
                bandDialogFragment
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _headerBinding = null
    }
}