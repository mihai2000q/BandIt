package com.bandit.ui.template

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.ui.component.AndroidComponents
import com.bandit.databinding.DialogFragmentSongBinding
import com.bandit.di.DILocator
import com.bandit.service.IValidatorService
import com.bandit.ui.songs.SongsViewModel
import com.bandit.util.AndroidUtils

abstract class SongDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentSongBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: SongsViewModel by activityViewModels()
    private lateinit var validatorService: IValidatorService

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentSongBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            TableRow.LayoutParams.WRAP_CONTENT
        )
        validatorService = DILocator.getValidatorService(super.requireActivity())
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

    protected open fun validateFields(): Boolean {
        return validatorService.validateName(binding.songEtName, binding.songEtNameLayout)
    }
}