package com.bandit.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import com.bandit.R
import com.bandit.constant.Constants

class LoadingDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        finish.observe(viewLifecycleOwner) {
            if(it)
                super.dismiss()
        }
        return inflater.inflate(R.layout.dialog_fragment_loading, container, false)
    }
    companion object {
        const val TAG = Constants.Component.LOADING_DIALOG_FRAGMENT
        val finish = MutableLiveData<Boolean>()
    }
}