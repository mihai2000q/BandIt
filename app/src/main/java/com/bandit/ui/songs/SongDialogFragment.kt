package com.bandit.ui.songs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.component.AndroidComponents
import com.bandit.databinding.DialogFragmentSongBinding
import com.bandit.util.AndroidUtils

abstract class SongDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentSongBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: SongsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    protected open fun validateFields(): Boolean {
        with(binding) {
            if (songEtName.text.isNullOrEmpty()) {
                songEtName.error = resources.getString(R.string.et_name_validation)
                return false
            }
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidComponents.datePickerDialog(super.requireContext(), songEtReleaseDate, true) {
                AndroidUtils.hideKeyboard(
                    super.requireActivity(),
                    Context.INPUT_METHOD_SERVICE,
                    songEtReleaseDate
                )
            }
            AndroidUtils.durationEditTextSetup(songEtDuration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}