package com.bandit.schedule

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.EventAdapter
import com.bandit.util.AndroidTestUtil
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.AndroidTestUtil.withIndex
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
@LargeTest
class ScheduleInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.navigation_schedule)).perform(click())
    }
    // Precondition - have a setup account with a band
    @Test
    fun schedule_fragment_ui() {
        // events mode
        onView(withId(R.id.schedule_bt_options)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.schedule_bt_calendar_mode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.schedule_search_view)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_calendar_view)).check(matches(not(isDisplayed())))
        try {
            // if there is a concert, then check this
            onView(withId(R.id.schedule_rv_events_view)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.schedule_rv_empty)).check(matches(isDisplayed()))
        }

        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_calendar_mode)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        //calendar mode
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.schedule_bt_calendar_mode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.schedule_calendar_view)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_search_view)).check(matches(not(isDisplayed())))
        try {
            // if there is a concert, then check this
            onView(withId(R.id.schedule_rv_events_view)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.schedule_rv_empty)).check(matches(isDisplayed()))
        }
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_add_remove_event() {
        val eventName = "New Event"
        this.addEvent(eventName)
        this.removeEvent(eventName)
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_edit_event() {
        val eventName = "Training Session"
        val newName = "Weekly Training Session"
        this.addEvent(eventName)
        this.editEvent(eventName, newName)
        this.removeEvent(newName)
    }
    @Test
    fun schedule_fragment_edit_remove_event_swipe_gestures() {
        val eventName = "New Event"
        val newName = "Weekly Training Session"
        this.addEvent(eventName)

        // edit event
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(eventName))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                hasDescendant(withText(eventName)), swipeRight()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))

        // remove event
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(newName))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                    hasDescendant(withText(newName)), swipeLeft()))
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(newName,
            "This event should have been deleted")
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_search_view_filter() {
        val eventName = "Training Session"
        val searchValue = "Training Ses"
        this.addEvent(eventName)

        onView(withId(R.id.schedule_search_view))
            .perform(typeText(searchValue), closeSoftKeyboard())

        onView(withText(eventName)).check(matches(isDisplayed()))

        onView(withId(R.id.schedule_search_view))
            .perform(typeText("2"), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(eventName,
            "This event should have been filtered out")

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_search_view))
            .perform(AndroidTestUtil.clearText(), typeText(searchValue), closeSoftKeyboard())

        this.removeEvent(eventName)
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_calendar_mode_add_remove_event() {
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_calendar_mode)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        val eventName = "New Event"

        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withText(LocalDate.now().toString())).check(matches(isDisplayed()))

        onView(withId(R.id.schedule_et_name)).perform(typeText(eventName))
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_duration))
            .perform(typeText("0130"), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(eventName)).check(matches(isDisplayed()))

        this.removeEvent(eventName)
    }
    @Test
    fun schedule_fragment_calendar_mode_swipe_gestures() {
        val eventName = "New Event Today"
        val newName = "Training Session"
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_calendar_mode)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withText(LocalDate.now().toString())).check(matches(isDisplayed()))

        onView(withId(R.id.schedule_et_name)).perform(typeText(eventName))
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_duration))
            .perform(typeText("0130"), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(eventName)).check(matches(isDisplayed()))

        // edit event
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(eventName))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                hasDescendant(withText(eventName)), swipeRight()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))

        // remove event
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(newName))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                hasDescendant(withText(newName)), swipeLeft()))
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(newName,
            "This event should have been deleted")
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_calendar_mode_filter_event() {
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_calendar_mode)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        val eventName = "New Event Today"
        // in add events we reopen the options menu
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        this.addEvent(eventName)
        val todayDate = LocalDate.now()
        val nextDay = todayDate.plusDays(1).dayOfMonth

        onView(withIndex(withText(nextDay.toString()), 0)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        AndroidTestUtil.checkIfItIsNotDisplayed(eventName,
            "It should have been filtered out")

        onView(withIndex(withText(todayDate.dayOfMonth.toString()), 0)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        this.removeEvent(eventName)
    }
    @Test
    fun schedule_fragment_manipulate_event_linked_to_concert() {
        val eventName = "New Concert in April"
        val newName = "New Concert in May"
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name)).perform(typeText(eventName))
        onView(withId(R.id.schedule_et_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_duration))
            .perform(typeText("0230"), closeSoftKeyboard())
        onView(withId(R.id.schedule_spinner_type)).perform(click())
        onView(withText("Concert")).inRoot(isPlatformPopup())
            .perform(click())

        onView(withId(R.id.schedule_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(eventName))))
        onView(withText(eventName)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_concerts)).perform(click())
        onView(withText(eventName)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_schedule)).perform(click())
        
        this.editEvent(eventName, newName)
        onView(withId(R.id.navigation_concerts)).perform(click())
        onView(withText(newName)).check(matches(isDisplayed()))
        AndroidTestUtil.checkIfItIsNotDisplayed(eventName, 
            "This concert should have been edited")
        onView(withId(R.id.navigation_schedule)).perform(click())
        
        this.removeEvent(newName)
        onView(withId(R.id.navigation_concerts)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(eventName,
            "This concert should have been removed")
    }
    private fun addEvent(name: String) {
        onView(withId(R.id.schedule_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name)).perform(typeText(name))
        onView(withId(R.id.schedule_et_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_duration))
            .perform(typeText("0030"), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(name))))
        onView(withText(name)).check(matches(isDisplayed()))
    }
    private fun editEvent(oldName: String, newName: String) {
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(oldName))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                hasDescendant(withText(oldName)), longClick()))
        onView(withText(R.string.bt_edit)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))
    }
    private fun removeEvent(name: String) {
        onView(withId(R.id.schedule_rv_events_view))
            .perform(RecyclerViewActions.scrollTo<EventAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(RecyclerViewActions.actionOnItem<EventAdapter.ViewHolder>(
                hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(name,
            "This event should have been deleted")
    }
}