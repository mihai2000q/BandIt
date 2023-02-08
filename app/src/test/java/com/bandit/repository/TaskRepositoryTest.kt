package com.bandit.repository

import com.bandit.data.model.Task
import com.bandit.data.repository.TaskRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class TaskRepositoryTest : BaseRepositoryTest<Task>() {
    private lateinit var taskRepository: TaskRepository
    @Before
    fun setup() {
        taskRepository = TaskRepository()
    }
    override fun import_data() {
        runBlocking {
            taskRepository.add(
                Task(
                    checked = false,
                    message = "Write lyrics for new song",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2022-10-12T10:00")
                )
            )
            taskRepository.add(
                Task(
                    checked = true,
                    message = "Come Up with idea for album",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2022-09-12T10:08")
                )
            )
            taskRepository.add(
                Task(
                    checked = false,
                    message = "Do the cover for new album",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2022-11-12T10:00")
                )
            )
            taskRepository.add(
                Task(
                    checked = false,
                    message = "Mix Master the last song",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2023-10-12T10:08")
                )
            )
            taskRepository.add(
                Task(
                    checked = true,
                    message = "Meet up with manager at some point to organize a concert",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2023-10-12T10:08")
                )
            )
            taskRepository.add(
                Task(
                    checked = true,
                    message = "Plan concert",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2023-12-12T10:08")
                )
            )
        }
    }
    @Test
    fun task_repository_add() {
        runBlocking {
            taskRepository.add(
                Task(
                    checked = true,
                    message = "This is a task",
                    bandId = -1,
                    createdOn = LocalDateTime.parse("2023-10-12T10:08")
                )
            )
        }
        assert_task(
            taskRepository,
            true,
            "This is a task",
            LocalDateTime.parse("2023-10-12T10:08")
        )
    }
    @Test
    fun task_repository_remove() {
        repository_remove(taskRepository)
    }
    @Test
    fun task_repository_edit() {
        import_data()
        var taskToEdit = taskRepository.list[0]
        val newTask = Task(
            checked = true,
            message = "Write lyrics for new new song",
            bandId = -1,
            createdOn = LocalDateTime.parse("2022-10-12T10:00")
        )
        // before
        assert_task(
            taskRepository,
            false,
            "Write lyrics for new song",
            LocalDateTime.parse("2022-10-12T10:00")
        )
        taskToEdit = Task(
            checked = newTask.checked,
            message = newTask.message,
            createdOn = newTask.createdOn,
            bandId = newTask.bandId,
            id = taskToEdit.id
        )
        runBlocking { taskRepository.edit(taskToEdit) }
        // after
        assert_task(
            taskRepository,
            true,
            "Write lyrics for new new song",
            LocalDateTime.parse("2022-10-12T10:00")
        )

    }
    @Test
    fun task_repository_different_ids() {
        different_ids(taskRepository, Task.EMPTY)
    }
    private fun assert_task(
        repository: TaskRepository,
        checked: Boolean,
        message: String,
        createdOn: LocalDateTime
    ) {
        assertNotNull(repository.list[0])
        assertEquals(checked, repository.list[0].checked)
        assertEquals(message, repository.list[0].message)
        assertEquals(createdOn, repository.list[0].createdOn)
    }
}