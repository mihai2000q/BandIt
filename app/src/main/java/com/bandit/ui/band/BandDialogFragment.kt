 package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandBinding

 class BandDialogFragment : DialogFragment() {

     private var _binding: DialogFragmentBandBinding? = null
     private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onDestroy() {
         super.onDestroy()
         _binding = null
     }

    companion object {
        const val TAG = Constants.Band.BAND_TAG
    }
}