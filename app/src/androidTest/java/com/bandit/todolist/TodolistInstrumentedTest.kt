package com.bandit.todolist

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.util.AndroidTestsUtil
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.TaskAdapter
import com.bandit.util.AndroidTestsUtil.withIndex
import com.bandit.util.ConstantsTest
import org.hamcrest.Matchers.not
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class TodolistInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    // Condition - have an account with a band already in place
    @Before
    fun setup() {
        AndroidTestsUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.main_drawer_layout)).perform(DrawerActions.open())
        onView(withId(R.id.navigation_todolist)).perform(click())
    }
    @Test
    fun todolist_fragment_ui() {
        onView(withId(R.id.todolist_bt_add)).check(matches(isDisplayed()))
        try {
            // if there is a task, then check this
            onView(withId(R.id.todolist_rv_tasks)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.todolist_rv_empty)).check(matches(isDisplayed()))
        }
    }
    // Condition - this is the only task in the view with that name
    @Test
    fun todolist_fragment_add_and_remove_task() {
        val taskName = "I should test at some point"
        this.addTask(taskName)
        this.removeFirstTask()
        onView(withText(taskName)).check(matches(not(isDisplayed())))
        try {
            // if there is a task, then check this
            onView(withText(taskName)).check(matches(not(isDisplayed())))
            fail("The task should have been deleted")
        } catch (_: AssertionError) { }
    }
    @Test
    fun todolist_fragment_click_edit_task() {
        val taskName = "I want to edit this task"
        val newName = "This task has been edited"
        this.addTask(taskName)

        onView(withId(R.id.todolist_rv_tasks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, click()))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, AndroidTestsUtil.clearText(R.id.task_et_message)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, typeText(newName)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, AndroidTestsUtil.pressEnter(R.id.task_et_message)))

        this.removeFirstTask()
    }
    @Test
    fun todolist_fragment_popup_edit_task() {
        val taskName = "I want to edit this task"
        val newName = "This task has been edited"
        this.addTask(taskName)

        onView(withId(R.id.todolist_rv_tasks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, longClick()
            ))

        onView(withText(R.string.bt_edit)).perform(click())

        onView(withId(R.id.todolist_rv_tasks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, AndroidTestsUtil.clearText(R.id.task_et_message)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, typeText(newName)))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, AndroidTestsUtil.pressEnter(R.id.task_et_message)))


        this.removeFirstTask()
    }
    private fun addTask(name: String) {
        try {
            onView(withText(name)).check(matches(isDisplayed()))
            fail("This task shouldn't be here before adding")
        } catch (_: NoMatchingViewException) {}
        onView(withId(R.id.todolist_bt_add)).perform(click())
        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.bottom_sheet_df_edit_text))
            .perform(typeText(name), pressImeActionButton())

        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.maximumDelayOperations))

        onView(withIndex(withText(name), 0)).check(matches(isDisplayed()))
    }
    private fun removeFirstTask() {
        onView(withId(R.id.todolist_rv_tasks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<TaskAdapter.ViewHolder>(
                0, longClick()
            ))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.maximumDelayOperations))
    }
}