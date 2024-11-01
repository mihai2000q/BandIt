package com.bandit.ui.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bandit.constant.Constants
import com.bandit.data.model.Task
import com.bandit.data.repository.TaskRepository
import com.bandit.di.DILocator
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {
    private val _database = DILocator.getDatabase()
    private val _repository = TaskRepository(_database)
    private val _tasks = MutableLiveData(_repository.list)
    val tasks: LiveData<List<Task>> = _tasks
    suspend fun addTask(task: Task) = coroutineScope {
        launch { _repository.add(task) }.join()
        this@TodoListViewModel.refresh()
    }
    suspend fun removeTask(task: Task) = coroutineScope {
        launch { _repository.remove(task) }.join()
        this@TodoListViewModel.refresh()
    }
    suspend fun editTask(task: Task) = viewModelScope.launch {
        launch { _repository.edit(task) }.join()
        this@TodoListViewModel.refresh()
    }

    private fun refresh() {
        _tasks.value = _repository.list
    }

    companion object {
        const val TAG = Constants.ToDoList.VIEW_MODEL_TAG
    }
}