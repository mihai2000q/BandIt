 package com.bandit.ui.band

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.databinding.DialogFragmentBandBinding
import com.bandit.ui.adapter.BandAdapter

 class BandDialogFragment : DialogFragment() {

     private var _binding: DialogFragmentBandBinding? = null
     private val binding get() = _binding!!
     private val viewModel: BandViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentBandBinding.inflate(inflater, container, false)
        return binding.root
    }

     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
         super.onViewCreated(view, savedInstanceState)
         with(binding) {
             bandBtInvite.setOnClickListener {
                 with(viewModel) {
                     email.value = bandEtEmail.text.toString()
                     sendBandInvitation()
                     bandEtEmail.setText("")
                 }
             }
             viewModel.members.observe(viewLifecycleOwner) {
                 bandMemberList.adapter = BandAdapter(it)
             }
         }
     }

     override fun onDestroy() {
         super.onDestroy()
         _binding = null
     }

    companion object {
        const val TAG = Constants.Band.BAND_TAG
    }
}