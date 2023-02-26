package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Task
import com.bandit.template.TemplateRepository

class TaskRepository(database: Database? = null)
    : TemplateRepository<Task>(database, database?.tasks) {
    override fun reassignId(item: Task): Task {
        var newTask = item
        while (isIdUsed(newTask.id)) {
            newTask = Task(
                item.checked,
                item.message,
                item.bandId,
                item.createdOn
            )
        }
        return newTask
    }
}