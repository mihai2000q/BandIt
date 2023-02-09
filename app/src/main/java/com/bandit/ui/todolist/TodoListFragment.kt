package com.bandit.ui.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.databinding.FragmentTodolistBinding
import com.bandit.ui.adapter.TaskAdapter
import com.bandit.util.AndroidUtils

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoListViewModel by activityViewModels()
    private val todoListAddDialogFragment = TodoListAddDialogFragment()

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
                todolistRvTasks.adapter = TaskAdapter(this@TodoListFragment, it.sorted(), viewModel)
            }
            todolistBtAdd.setOnClickListener { addButton() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addButton() {
        AndroidUtils.showDialogFragment(
            todoListAddDialogFragment,
            childFragmentManager
        )
    }

}