package com.bandit.ui.songs.albums

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.builder.AndroidComponents
import com.bandit.databinding.DialogFragmentAlbumBinding
import com.bandit.ui.songs.SongsViewModel

open class AlbumDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentAlbumBinding? = null
    protected val binding get() = _binding!!
    protected val viewModel: SongsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentAlbumBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val datePickerDialog = AndroidComponents.datePickerDialog(super.requireContext(), albumEtReleaseDate, true)
            albumEtReleaseDate.setOnClickListener { datePickerDialog.show() }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}