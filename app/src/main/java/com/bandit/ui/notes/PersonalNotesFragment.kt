package com.bandit.ui.notes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.data.model.Note
import com.bandit.databinding.FragmentPersonalNotesBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.NoteAdapter
import com.bandit.util.AndroidUtils

class PersonalNotesFragment : Fragment() {

    private var _binding: FragmentPersonalNotesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalNotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalNotesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            personalNotesBtAdd.setOnClickListener { addNote() }
            viewModel.notes.observe(viewLifecycleOwner) {
                personalNotesList.adapter = NoteAdapter(this@PersonalNotesFragment, it.sorted(), viewModel)
            }
        }
    }

    private fun addNote() {
        viewModel.addNote(
            Note(
                resources.getString(R.string.et_title),
                resources.getString(R.string.default_note_message),
                DILocator.database.currentAccount.id
            )
        )
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.note_add_toast)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}