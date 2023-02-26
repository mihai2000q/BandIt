package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Note
import com.bandit.template.TemplateRepository

class NoteRepository(database: Database? = null)
    : TemplateRepository<Note>(database, database?.notes) {
    override fun reassignId(item: Note): Note {
        var newNote: Note = item
        while (isIdUsed(newNote.id)) {
            newNote = Note(
                title = newNote.title,
                content = newNote.content,
                createdOn = newNote.createdOn,
                accountId = newNote.accountId
            )
        }
        return newNote
    }
}