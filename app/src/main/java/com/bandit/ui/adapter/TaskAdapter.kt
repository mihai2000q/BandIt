package com.bandit.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.bandit.R
import com.bandit.data.model.Task
import com.bandit.databinding.ModelTaskBinding
import com.bandit.ui.todolist.TodoListViewModel
import com.bandit.util.AndroidUtils

class TaskAdapter(
    private val tasks: List<Task>,
    private val viewModel: TodoListViewModel
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
                taskTvMessage.text = task.message
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
        AndroidUtils.toastNotification(
            holder.binding.root.context,
            holder.binding.root.resources.getString(R.string.task_remove_toast)
        )
        return viewModel.removeTask(task)
    }

    private fun onEdit(holder: ViewHolder, task: Task): Boolean {
        viewModel.selectedTask.value = task
        with(holder.binding) {
            val message = taskTvMessage.text.toString()
            taskLayout.removeView(taskTvMessage)
            taskLayout.addView(editText(root.context, message))
        }
        return true
    }

    private fun editText(context: Context, message: String): EditText {
        val editText = EditText(context)
        editText.setText(message)
        editText.requestFocus()
        return editText
    }

}