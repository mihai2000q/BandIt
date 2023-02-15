package com.bandit.model

import com.bandit.constant.Constants
import com.bandit.data.model.Note
import org.junit.Assert
import org.junit.Test
import java.time.LocalDateTime

class NoteTest {
    @Test
    fun note_init() {
        val note = Note(
            title = "This is a title",
            content = "This is the content page",
            accountId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        Assert.assertEquals("This is a title", note.title)
        Assert.assertEquals("This is the content page", note.content)
        Assert.assertEquals(LocalDateTime.parse("2023-10-12T10:08"), note.createdOn)
        Assert.assertNotNull(note.id)
    }
    @Test
    fun note_init_id() {
        //Repeated tests improvised
        for (i in 1..100) {
            val note = Note.EMPTY
            Assert.assertNotNull(note.id)
            if (note.id < 0 || note.id > Constants.MAX_NR_ITEMS)
                Assert.fail("The Id should be between these boundaries")
        }
    }
    @Test
    fun note_equals() {
        val note = Note(
            title = "This is a title",
            content = "This is the content page",
            accountId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        val note2 = Note(
            title = "This is a title2",
            content = "This is the content page",
            accountId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        val note3 = Note(
            title = "This is a title",
            content = "This is the content page",
            accountId = -1,
            createdOn = LocalDateTime.parse("2023-10-12T10:08")
        )
        Assert.assertEquals(note, note3)
        Assert.assertNotEquals(note, note2)
        Assert.assertNotEquals(note.id, note3.id)
    }
    @Test
    fun note_sort() {
        val outcome = mutableListOf(
            Note(
                title = "title 1",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:00")
            ),
            Note(
                title = "title 4",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-11-12T10:00")
            ),
            Note(
                title = "title 2",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-20T10:00")
            ),
            Note(
                title = "title 3",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-25T10:00")
            )
        )
        outcome.sort()
        val expected = listOf(
            Note(
                title = "title 1",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-12T10:00")
            ),
            Note(
                title = "title 2",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-20T10:00")
            ),
            Note(
                title = "title 3",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-10-25T10:00")
            ),
            Note(
                title = "title 4",
                content = "content",
                accountId = -1,
                createdOn = LocalDateTime.parse("2023-11-12T10:00")
            )
        )
        Assert.assertEquals(outcome, expected)
    }
}