package com.bandit.ui.songs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.builder.AndroidComponents
import com.bandit.databinding.DialogFragmentSongBinding

open class SongDialogFragment : DialogFragment() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val datePickerDialog = AndroidComponents.datePickerDialog(super.requireContext(), songEtReleaseDate)
            songEtReleaseDate.setOnClickListener { datePickerDialog.show() }
            songEtDuration.addTextChangedListener {
                if(it.toString().length == 2) {
                    songEtDuration.setText(buildString {
                        append(songEtDuration.text)
                        append(":")
                    })
                    songEtDuration.setSelection(3)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}