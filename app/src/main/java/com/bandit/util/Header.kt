package com.bandit.util

import android.widget.Button
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.bandit.data.model.Band
import com.bandit.ui.account.AccountDialogFragment
import com.bandit.ui.band.BandDialogFragment
import com.bandit.ui.band.CreateBandDialogFragment

class Header(
    activity: FragmentActivity,
    accountButton: ImageButton,
    bandButton: Button,
    viewLifecycleOwner: LifecycleOwner,
    band: LiveData<Band>
) {
    init {
        val accountDialogFragment = AccountDialogFragment(accountButton)
        val createBandDialogFragment = CreateBandDialogFragment()
        val bandDialogFragment = BandDialogFragment()
        AndroidUtils.accountButton(
            activity,
            accountButton,
            accountDialogFragment
        )
        AndroidUtils.bandButton(
            activity,
            bandButton,
            band,
            viewLifecycleOwner,
            createBandDialogFragment,
            bandDialogFragment
        )
    }
}