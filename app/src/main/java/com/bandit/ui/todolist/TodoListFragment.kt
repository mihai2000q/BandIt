package com.bandit.ui.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.data.model.Task
import com.bandit.databinding.FragmentTodolistBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.TaskAdapter
import com.bandit.util.AndroidUtils

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoListViewModel by activityViewModels()
    private val _database = DILocator.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewModel.tasks.observe(viewLifecycleOwner) {
                todolistRvTasks.adapter = TaskAdapter(this@TodoListFragment, it, viewModel)
            }
            todolistBtAdd.setOnClickListener {
                AndroidUtils.loadTask(this@TodoListFragment) { addButton() }
                todolistRvTasks.adapter = TaskAdapter(this@TodoListFragment, it.sorted(), viewModel)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun addButton() {
        viewModel.addTask(
            Task(
                false,
                resources.getString(R.string.default_task_message),
                _database.currentBand.id
            )
        )
        AndroidUtils.toastNotification(
            super.requireContext(),
            resources.getString(R.string.task_add_toast)
        )
    }

}