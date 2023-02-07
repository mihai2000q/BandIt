package com.bandit.data.repository

import com.bandit.data.db.Database
import com.bandit.data.model.Note

class NoteRepository(database: Database? = null)
    : BaseRepository<Note>(database, database?.notes) {
    override fun reassignId(item: Note): Note {
        var newNote: Note = item
        while (isIdUsed(newNote.id)) {
            newNote = Note(
                title = newNote.title,
                content = newNote.content,
                createdOn = newNote.createdOn
            )
        }
        return newNote
    }
}