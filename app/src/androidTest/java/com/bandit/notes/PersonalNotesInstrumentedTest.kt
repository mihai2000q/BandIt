package com.bandit.notes

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.NoteAdapter
import com.bandit.util.AndroidTestUtil
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.AndroidTestUtil.withIndex
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class PersonalNotesInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    // Precondition - have an account already setup
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.main_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigation_personal_notes)).perform(click())
    }
    @Test
    fun personal_notes_fragment_ui() {
        onView(withId(R.id.personal_notes_bt_add)).check(matches(isDisplayed()))
        try {
            // if there is a note, then check this
            onView(withId(R.id.personal_notes_list)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.personal_notes_rv_empty)).check(matches(isDisplayed()))
        }
    }
    // Condition - this is the only note in the view with that name
    @Test
    fun personal_notes_fragment_add_and_remove_note() {
        this.addNote()
        this.removeFirstNote()
        AndroidTestUtil.checkIfItIsNotDisplayed(withText(R.string.default_note_message),
            "This note should have been deleted")
    }
    @Test
    fun personal_notes_fragment_click_edit_note() {
        val newTitle = "don't forget the lyrics"
        val newMessage = "I should probably not forget to write the lyrics for my beautiful band"
        this.addNote()

        onView(withId(R.id.personal_notes_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<NoteAdapter.ViewHolder>(
                0, click()))

        onView(withId(R.id.personal_notes_edit_title))
            .perform(clearText(), typeText(newTitle))
        onView(withId(R.id.personal_notes_edit_content))
            .perform(clearText(), typeText(newMessage), pressImeActionButton())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newTitle)).check(matches(isDisplayed()))
        onView(withText(newMessage)).check(matches(isDisplayed()))

        this.removeFirstNote()
    }
    @Test
    fun personal_notes_fragment_popup_edit_note() {
        val newTitle = "don't forget the lyrics"
        val newMessage = "I should probably not forget to write the lyrics for my beautiful band"
        this.addNote()

        onView(withId(R.id.personal_notes_list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<NoteAdapter.ViewHolder>(
                0, longClick()
                ))
        onView(withText(R.string.bt_edit)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.personal_notes_edit_title))
            .perform(clearText(), typeText(newTitle))
        onView(withId(R.id.personal_notes_edit_content))
            .perform(clearText(), typeText(newMessage), pressImeActionButton())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newTitle)).check(matches(isDisplayed()))
        onView(withText(newMessage)).check(matches(isDisplayed()))

        this.removeFirstNote()
    }
    private fun addNote() {
        AndroidTestUtil.checkIfItIsNotDisplayed(withText(R.string.default_note_message),
            "This note shouldn't be here before adding")
        onView(withId(R.id.personal_notes_bt_add)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withIndex(withText(R.string.default_note_message), 0))
            .check(matches(isDisplayed()))
        onView(withIndex(withText(R.string.et_title), 0))
            .check(matches(isDisplayed()))
    }
    private fun removeFirstNote() {
        onView(withId(R.id.personal_notes_list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<NoteAdapter.ViewHolder>(
                0, longClick()
            ))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
}