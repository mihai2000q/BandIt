package com.bandit.ui.notes

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bandit.constant.Constants
import com.bandit.data.model.Note
import com.bandit.databinding.DialogFragmentEditPersonalNoteBinding
import com.bandit.util.AndroidUtils

class PersonalNotesEditDialogFragment : DialogFragment() {
    private var _binding: DialogFragmentEditPersonalNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PersonalNotesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentEditPersonalNoteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.dialog?.window?.setLayout(
            AndroidUtils.getScreenWidth(super.requireActivity()),
            AndroidUtils.getScreenHeight(super.requireActivity()) * 7 / 8
        )
        with(binding) {
            viewModel.selectedNote.observe(viewLifecycleOwner) {
                personalNotesEditTitle.setText(it.title)
                personalNotesEditContent.setText(it.content)
            }
            personalNotesEditContent.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    super.dismiss()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        AndroidUtils.loadDialogFragment(this) {
            viewModel.editNote(
                Note(
                    title = binding.personalNotesEditTitle.text.toString(),
                    content = binding.personalNotesEditContent.text.toString(),
                    accountId = viewModel.selectedNote.value!!.accountId,
                    createdOn = viewModel.selectedNote.value!!.createdOn,
                    id = viewModel.selectedNote.value!!.id
                )
            )
        }
    }

    companion object {
        const val TAG = Constants.PersonalNotes.EDIT_TAG
    }
}