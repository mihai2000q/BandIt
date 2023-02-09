package com.bandit.ui.todolist

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.constant.Constants
import com.bandit.data.model.Task
import com.bandit.databinding.DialogFragmentTodolistAddBinding
import com.bandit.di.DILocator
import com.bandit.util.AndroidUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TodoListAddDialogFragment : BottomSheetDialogFragment() {

    private var _binding: DialogFragmentTodolistAddBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentTodolistAddBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            AndroidUtils.showKeyboard(super.requireActivity(), Context.INPUT_METHOD_SERVICE, todolistAddEtTask)
            todolistAddEtTask.requestFocus()
            todolistAddEtTask.setOnKeyListener { _, keyCode, event ->
                if((event.action == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    super.dismiss()
                    return@setOnKeyListener true
                }
                return@setOnKeyListener false
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(binding.todolistAddEtTask.text.isNullOrEmpty()) return
        viewModel.addTask(
            Task(
                checked = false,
                message = binding.todolistAddEtTask.text.toString(),
                bandId = DILocator.database.currentBand.id
            )
        )
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.task_add_toast)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = Constants.ToDoList.ADD_TAG
    }
}