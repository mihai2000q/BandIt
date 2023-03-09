package com.bandit.ui.todolist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.bandit.R
import com.bandit.ui.component.AndroidComponents
import com.bandit.ui.component.TypingBottomSheetDialogFragment
import com.bandit.data.model.Task
import com.bandit.databinding.FragmentTodolistBinding
import com.bandit.ui.adapter.TaskAdapter
import com.bandit.ui.band.BandViewModel
import com.bandit.util.AndroidUtils

class TodoListFragment : Fragment() {

    private var _binding: FragmentTodolistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TodoListViewModel by activityViewModels()
    private val bandViewModel: BandViewModel by activityViewModels()

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
            AndroidUtils.loadDialogFragment(viewModel.viewModelScope, this) {
                viewModel.addTask(
                    Task(
                        checked = false,
                        message = it.text.toString(),
                        bandId = bandViewModel.band.value!!.id
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
                bandViewModel.band,
                {
                    return@setRecyclerViewEmpty TaskAdapter(
                        this@TodoListFragment, it.sorted().asReversed(), viewModel)
                }
            )
            AndroidUtils.disableIfBandEmpty(
                viewLifecycleOwner,
                resources,
                bandViewModel.band,
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