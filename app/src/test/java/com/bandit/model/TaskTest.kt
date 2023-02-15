package com.bandit.model

import com.bandit.constant.Constants
import com.bandit.data.model.Task
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime

class TaskTest {
    @Test
    fun task_init() {
        val task = Task(
            checked = false,
            message = "This is a task",
            bandId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        Assert.assertEquals(task.checked, false)
        Assert.assertEquals(task.message, "This is a task")
        Assert.assertEquals(task.createdOn, LocalDateTime.parse("2023-10-12T10:08"))
        Assert.assertNotNull(task.id)
    }
    @Test
    fun task_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val task = Task.EMPTY
            Assert.assertNotNull(task.id)
            if (task.id < 0 || task.id > Constants.MAX_NR_ITEMS)
                Assert.fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun task_equals() {
        val task = Task(
            checked = false,
            message = "This is a task",
            bandId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        val task2 = Task(
            checked = false,
            message = "This is a task2",
            bandId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        val task3 = Task(
            checked = false,
            message = "This is a task",
            bandId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        Assert.assertEquals(task, task3)
        Assert.assertNotEquals(task, task2)
        Assert.assertNotEquals(task.id, task3.id)
    }
    @Test
    fun task_sort() {
        val outcome = mutableListOf(
            Task(
                checked = false,
                message = "Task 3",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:09")
            ),
            Task(
                checked = false,
                message = "Task 2",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:08")
            ),
            Task(
                checked = false,
                message = "Task 1",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:07")
            ),
            Task(
                checked = false,
                message = "Task 4",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-13T10:08")
            )
        )
        outcome.sort()
        val expected = listOf(
            Task(
                checked = false,
                message = "Task 1",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:07")
            ),
            Task(
                checked = false,
                message = "Task 2",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:08")
            ),
            Task(
                checked = false,
                message = "Task 3",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:09")
            ),
            Task(
                checked = false,
                message = "Task 4",
                bandId = -1,
                createdOn = LocalDateTime.parse("2023-10-13T10:08")
            )
        )
        Assert.assertEquals(outcome, expected)
    }
}