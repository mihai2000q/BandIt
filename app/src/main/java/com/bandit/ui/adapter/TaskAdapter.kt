package com.bandit.ui.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Task
import com.bandit.databinding.ModelTaskBinding
import com.bandit.ui.todolist.TodoListViewModel
import com.bandit.util.AndroidUtils

class TaskAdapter(
    private val tasks: List<Task>,
    private val viewModel: TodoListViewModel,
    private val activity: Activity
) : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {
    private lateinit var popupMenu: PopupMenu
    private var isPopupShown = false

    inner class ViewHolder(val binding: ModelTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ModelTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return tasks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]

        with(holder) {
            itemView.setOnLongClickListener { onLongClick(holder, task) }
            with(binding) {
                taskCheckBox.isChecked = task.checked
                if(taskCheckBox.isChecked)
                    taskTvMessage.paintFlags = taskTvMessage.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                else
                    taskTvMessage.paintFlags = taskTvMessage.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    viewModel.editTask(Task(
                        isChecked,
                        task.message,
                        task.bandId,
                        task.createdOn,
                        task.id
                    ))
                }
                taskTvMessage.text = task.message
                taskTvMessage.setOnLongClickListener { onLongClick(holder, task) }
                taskTvMessage.setOnClickListener { onEdit(holder, task) }
            }
        }
    }

    private fun popupMenu(holder: ViewHolder, task: Task) {
        popupMenu = PopupMenu(holder.binding.root.context, holder.itemView)
        popupMenu.inflate(R.menu.item_popup_menu)
        popupMenu.setOnDismissListener { isPopupShown = false }
        popupMenu.setOnMenuItemClickListener {
            popupMenu.dismiss()
            when (it.itemId) {
                R.id.popup_menu_delete -> onDelete(holder, task)
                else -> onEdit(holder, task)
            }
        }
    }

    private fun onLongClick(holder: ViewHolder, task: Task): Boolean {
        popupMenu(holder, task)
        if(!isPopupShown) {
            isPopupShown = true
            popupMenu.show()
        }
        return true
    }

    private fun onDelete(holder: ViewHolder, task: Task): Boolean {
        viewModel.removeTask(task)
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.task_remove_toast)
        )
        return true
    }

    private fun onEdit(holder: ViewHolder, task: Task): Boolean {
        with(holder.binding) {
            taskEtMessage.setText(task.message)
            taskEtMessage.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    viewModel.editTask(
                        Task(
                            checked = task.checked,
                            message = taskEtMessage.text.toString(),
                            bandId = task.bandId,
                            createdOn = task.createdOn,
                            id = task.id
                        )
                    )
                    AndroidUtils.toastNotification(
                        holder.binding.root.context,
                        holder.binding.root.resources.getString(R.string.task_edit_toast)
                    )
                    taskEtMessage.clearFocus()
                    AndroidUtils.hideKeyboard(activity, Context.INPUT_METHOD_SERVICE, taskEtMessage)
                    taskViewSwitcher.showNext()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
            taskEtMessage.setOnFocusChangeListener { view, hasFocus ->
                if(!hasFocus) {
                    view.clearFocus()
                    AndroidUtils.hideKeyboard(activity, Context.INPUT_METHOD_SERVICE, taskEtMessage)
                    taskViewSwitcher.showNext()
                }
            }
            taskViewSwitcher.showNext()
            taskEtMessage.requestFocus()
            AndroidUtils.showKeyboard(activity, Context.INPUT_METHOD_SERVICE, taskEtMessage)
        }
        return true
    }
}