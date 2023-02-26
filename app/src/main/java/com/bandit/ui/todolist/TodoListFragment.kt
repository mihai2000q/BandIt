package com.bandit.ui.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.component.TypingBottomSheetDialogFragment
import com.bandit.data.model.Task
import com.bandit.databinding.FragmentTodolistBinding
import com.bandit.di.DILocator
import com.bandit.ui.adapter.TaskAdapter
import com.bandit.util.AndroidUtils

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoListViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodolistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val typingBottomSheetDialogFragment = TypingBottomSheetDialogFragment {
            if(it.text.isNullOrEmpty()) return@TypingBottomSheetDialogFragment
            AndroidUtils.loadDialogFragment(this) {
                viewModel.addTask(
                    Task(
                        checked = false,
                        message = it.text.toString(),
                        bandId = DILocator.getDatabase().currentBand.id
                    )
                )
                AndroidComponents.toastNotification(
                    super.requireContext(),
                    resources.getString(R.string.task_add_toast)
                )
            }
        }
        with(binding) {
            AndroidUtils.setRecyclerViewEmpty(
                viewLifecycleOwner,
                viewModel.tasks,
                todolistRvTasks,
                todolistRvEmpty,
                todolistRvBandEmpty,
                {
                    return@setRecyclerViewEmpty TaskAdapter(
                        this@TodoListFragment, it.sorted(), viewModel)
                }
            )
            viewModel.tasks.observe(viewLifecycleOwner) {
                if(DILocator.getDatabase().currentBand.isEmpty()) {
                    todolistRvTasks.visibility = View.GONE
                    todolistRvBandEmpty.visibility = View.VISIBLE
                }
                else if(it.isEmpty()) {
                    todolistRvTasks.visibility = View.GONE
                    todolistRvEmpty.visibility = View.VISIBLE
                } else {
                    todolistRvTasks.adapter =
                        TaskAdapter(this@TodoListFragment, it.sorted(), viewModel)
                    todolistRvTasks.visibility = View.VISIBLE
                    todolistRvEmpty.visibility = View.GONE
                    todolistRvBandEmpty.visibility = View.GONE
                }
            }
            AndroidUtils.disableIfBandNull(
                resources,
                DILocator.getDatabase().currentBand,
                todolistBtAdd
            ) {
                AndroidUtils.showDialogFragment(
                    typingBottomSheetDialogFragment,
                    childFragmentManager
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}