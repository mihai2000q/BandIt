package com.bandit.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Note
import com.bandit.databinding.DialogFragmentEditPersonalNoteBinding
import com.bandit.ui.component.AndroidComponents
import com.bandit.util.AndroidUtils
import java.time.LocalDateTime

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
            TableRow.LayoutParams.WRAP_CONTENT
        )
        with(binding) {
            viewModel.selectedNote.observe(viewLifecycleOwner) {
                personalNotesEtTitle.setText(it.title)
                personalNotesEtContent.setText(it.content)
            }
            personalNotesEditBtSend.setOnClickListener {
                if(personalNotesEtTitle.text.toString() == viewModel.selectedNote.value!!.title &&
                   personalNotesEtContent.text.toString() == viewModel.selectedNote.value!!.content)
                    return@setOnClickListener
                AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                    this@PersonalNotesEditDialogFragment) {
                    viewModel.editNote(
                        Note(
                            title = binding.personalNotesEtTitle.text.toString(),
                            content = binding.personalNotesEtContent.text.toString(),
                            accountId = viewModel.selectedNote.value!!.accountId,
                            createdOn = LocalDateTime.now(),
                            id = viewModel.selectedNote.value!!.id
                        )
                    )
                }
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.note_edit_toast)
                )
                super.dismiss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.PersonalNotes.EDIT_TAG
    }
}