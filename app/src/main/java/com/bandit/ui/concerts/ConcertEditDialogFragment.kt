package com.bandit.ui.concerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.DialogFragmentConcertEditBinding
import com.bandit.constant.Constants

class ConcertEditDialogFragment : DialogFragment() {

    private var binding: DialogFragmentConcertEditBinding? = null
    private val viewModel: ConcertsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //TODO:NOT IMPLEMENTED YET
        binding = DialogFragmentConcertEditBinding.inflate(layoutInflater, container, false)
        return binding?.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        const val TAG = Constants.Concert.EDIT_CONCERT_TAG
    }
}