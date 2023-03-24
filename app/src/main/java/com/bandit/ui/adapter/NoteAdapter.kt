package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Note
import com.bandit.databinding.ModelNoteBinding
import com.bandit.extension.printName
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.notes.PersonalNotesEditDialogFragment
import com.bandit.ui.notes.PersonalNotesViewModel
import com.bandit.util.AndroidUtils

class NoteAdapter(
    private val fragment: Fragment,
    private val notes: List<Note>,
    private val viewModel: PersonalNotesViewModel
) : RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private val personalNotesEditDialogFragment = PersonalNotesEditDialogFragment()
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

    inner class ViewHolder(val binding: ModelNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelNoteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]

        with(holder) {
            itemView.setOnLongClickListener { onLongClick(holder, note) }
            with(binding) {
                noteDate.text = note.createdOn.toLocalDate().printName()
                noteTitle.text = note.title
                noteMessage.text = note.content
                noteMessage.setOnClickListener { onEdit(note) }
                noteMessage.setOnLongClickListener { onLongClick(holder, note) }
            }
        }
    }

    private fun popupMenu(holder: NoteAdapter.ViewHolder, note: Note) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(holder, note)
                else -> { onEdit(note) }
            }
        }
    }

    private fun onLongClick(holder: NoteAdapter.ViewHolder, note: Note): Boolean {
        popupMenu(holder, note)
        if(!isPopupShown) {
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

    private fun onDelete(holder: NoteAdapter.ViewHolder, note: Note): Boolean {
        AndroidComponents.alertDialog(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.note_alert_dialog_title),
            holder.binding.root.resources.getString(R.string.note_alert_dialog_message),
            holder.binding.root.resources.getString(R.string.alert_dialog_positive),
            holder.binding.root.resources.getString(R.string.alert_dialog_negative)
        ) {
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope,
                fragment) { viewModel.removeNote(note) }
            AndroidComponents.toastNotification(
                holder.binding.root.context,
                holder.binding.root.resources.getString(R.string.note_remove_toast)
            )
        }
        return true
    }

    private fun onEdit(note: Note): Boolean {
        viewModel.selectedNote.value = note
        AndroidUtils.showDialogFragment(
            personalNotesEditDialogFragment,
            fragment.childFragmentManager
        )
        return true
    }

}