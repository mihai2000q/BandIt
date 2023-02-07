package com.bandit.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Event
import com.bandit.databinding.ModelEventBinding
import com.bandit.ui.schedule.ScheduleDetailDialogFragment
import com.bandit.ui.schedule.ScheduleEditDialogFragment
import com.bandit.ui.schedule.ScheduleViewModel
import com.bandit.util.AndroidUtils

class EventAdapter(
    private val events: List<Event>,
    private val viewModel: ScheduleViewModel,
    private val childFragmentManager: FragmentManager
) : RecyclerView.Adapter<EventAdapter.ViewHolder>() {
    private val scheduleDetailDialogFragment = ScheduleDetailDialogFragment()
    private val scheduleEditDialogFragment = ScheduleEditDialogFragment()
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
                eventTimeName.text = buildString {
                    append(event.dateTime.toLocalTime().toString())
                    append(" ")
                    append(event.name)
                }
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
                R.id.popup_menu_delete -> onDelete(holder, event)
                else -> onEdit(event)
            }
        }
    }

    private fun onClick(event: Event) {
        viewModel.selectedEvent.value = event
        AndroidUtils.showDialogFragment(
            scheduleDetailDialogFragment,
            childFragmentManager
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

    private fun onDelete(holder: ViewHolder, event: Event): Boolean {
        viewModel.removeEvent(event)
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.event_remove_toast)
        )
        return true
    }
    private fun onEdit(event: Event): Boolean {
        viewModel.selectedEvent.value = event
        AndroidUtils.showDialogFragment(
            scheduleEditDialogFragment,
            childFragmentManager
        )
        return true
    }

}