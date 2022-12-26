package com.bandit.ui.todolist

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bandit.databinding.FragmentTodolistBinding

class TodoListFragment : Fragment() {

    private lateinit var binding: FragmentTodolistBinding
    private lateinit var viewModel: TodoListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTodolistBinding.inflate(layoutInflater, container, false)

        viewModel = ViewModelProvider(this)[TodoListViewModel::class.java]

        return binding.root
    }
}