package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.data.model.Event
import com.bandit.databinding.ModelEventBinding
import com.bandit.ui.schedule.ScheduleDetailDialogFragment
import com.bandit.ui.schedule.ScheduleEditDialogFragment
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils

class EventAdapter(
    private val fragment: Fragment,
    private val events: List<Event>,
    private val viewModel: ScheduleViewModel,
    private val onDeleteEvent: (Event) -> Unit,
    private val onEditEvent: (Event) -> Unit
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    private val scheduleDetailDialogFragment = ScheduleDetailDialogFragment()
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

    inner class ViewHolder(val binding: ModelEventBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val event = events[position]

        with(holder) {
            itemView.setOnClickListener { onClick(event) }
            itemView.setOnLongClickListener { onLongClick(holder, event) }
            with(binding) {
                eventDate.text = event.printExplicitDateTime()
                eventTime.text = event.dateTime.toLocalTime().toString()
                eventName.text = event.name
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, event: Event) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(event)
                else -> onEdit(event)
            }
        }
    }

    private fun onClick(event: Event) {
        viewModel.selectedEvent.value = event
        AndroidUtils.showDialogFragment(
            scheduleDetailDialogFragment,
            fragment.childFragmentManager
        )
    }

    private fun onLongClick(holder: ViewHolder, event: Event): Boolean {
        popupMenu(holder, event)
        if(!isPopupShown) {
            popupMenu.show()
            isPopupShown = true
        }
        return true
    }

    private fun onDelete(event: Event): Boolean {
        onDeleteEvent(event)
        return true
    }
    private fun onEdit(event: Event): Boolean {
        onEditEvent(event)
        return true
    }

}