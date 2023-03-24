package com.bandit.repository

import com.bandit.data.model.Note
import com.bandit.data.repository.NoteRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class NoteRepositoryTest : BaseRepositoryTest<Note>() {
    private lateinit var noteRepository: NoteRepository
    @Before
    fun setup() {
        noteRepository = NoteRepository()
    }
    override fun import_data() {
        runBlocking {
            noteRepository.add(
                Note(
                    title = "Talk drummer",
                    content = "I should talk with Tim, so that we make a nice riff",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2022-10-12T10:00")
                )
            )
            noteRepository.add(
                Note(
                    title = "write lyrics for new song",
                    content = "",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2023-02-12T10:00")
                )
            )
            noteRepository.add(
                Note(
                    title = "make up some badass riff",
                    content = "",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2023-05-12T10:00")
                )
            )
            noteRepository.add(
                Note(
                    title = "do the preselection",
                    content = "Talk with new people in search for a drummer",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2022-04-12T10:00")
                )
            )
            noteRepository.add(
                Note(
                    title = "This is only a title",
                    content = "This is only the content of the note",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2023-06-12T10:00")
                )
            )
            noteRepository.add(
                Note(
                    title = "This is a title",
                    content = "This is the content of the note",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2023-05-26T10:00")
                )
            )
        }
    }
    @Test
    fun note_repository_add() {
        runBlocking {
            noteRepository.add(
                Note(
                    title = "This is a title",
                    content = "This is the content of the note",
                    accountId = -1,
                    createdOn = LocalDateTime.parse("2023-10-12T10:00")
                )
            )
        }
        assert_note(
            noteRepository,
            "This is a title",
            "This is the content of the note",
            LocalDateTime.parse("2023-10-12T10:00")
        )
    }
    @Test
    fun note_repository_remove() {
        repository_remove(noteRepository)
    }
    @Test
    fun note_repository_edit() {
        import_data()
        var noteToEdit = noteRepository.list[0]
        val newNote = Note(
            title = "New title",
            content = "New content",
            accountId = -1,
            createdOn = LocalDateTime.parse("2023-12-12T12:00")
        )
        //before
        assert_note(
            noteRepository,
            "Talk drummer",
            "I should talk with Tim, so that we make a nice riff",
            LocalDateTime.parse("2022-10-12T10:00")
        )
        noteToEdit = Note(
            title = newNote.title,
            content = newNote.content,
            accountId = newNote.accountId,
            createdOn = newNote.createdOn,
            id = noteToEdit.id
        )
        runBlocking { noteRepository.edit(noteToEdit) }
        //after
        assert_note(
            noteRepository,
            "New title",
            "New content",
            LocalDateTime.parse("2023-12-12T12:00")
        )
    }
    @Test
    fun note_repository_different_ids() {
        different_ids(noteRepository, Note.EMPTY)
    }
    private fun assert_note(
        repository: NoteRepository,
        title: String,
        content: String,
        createdOn: LocalDateTime
    ) {
        assertNotNull(repository.list[0])
        assertEquals(title, repository.list[0].title)
        assertEquals(content, repository.list[0].content)
        assertEquals(createdOn, repository.list[0].createdOn)
    }
}