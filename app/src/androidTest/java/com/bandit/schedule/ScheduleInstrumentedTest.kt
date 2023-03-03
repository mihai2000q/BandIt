package com.bandit.schedule

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.util.AndroidTestsUtil
import com.bandit.util.AndroidTestsUtil.waitFor
import com.bandit.util.AndroidTestsUtil.withIndex
import com.bandit.util.ConstantsTest
import org.junit.Assert
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
        AndroidTestsUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.navigation_schedule)).perform(click())
    }
    // Condition - have a setup account with a band
    @Test
    fun concerts_fragment_ui() {
        // events mode
        onView(withId(R.id.schedule_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_search_view)).check(matches(isDisplayed()))
        try {
            onView(withId(R.id.schedule_spinner_mode)).check(matches(isDisplayed()))
            Assert.fail("The Schedule Spinner 'Day/Week/Month' View should be invisible")
        } catch (_: AssertionError) {}
        try {
            onView(withId(R.id.schedule_calendar_view)).check(matches(isDisplayed()))
            Assert.fail("The Schedule Calendar View should be invisible")
        } catch (_: AssertionError) {}
        try {
            // if there is a concert, then check this
            onView(withId(R.id.schedule_events_view)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.schedule_rv_empty)).check(matches(isDisplayed()))
        }

        onView(withId(R.id.schedule_switch_view)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        //calendar mode
        onView(withId(R.id.schedule_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_spinner_mode)).check(matches(isDisplayed()))
        onView(withId(R.id.schedule_calendar_view)).check(matches(isDisplayed()))
        try {
            onView(withId(R.id.schedule_search_view)).check(matches(isDisplayed()))
            Assert.fail("The Schedule Search View should be invisible")
        } catch (_: AssertionError) {}
        try {
            // if there is a concert, then check this
            onView(withId(R.id.schedule_events_view)).check(matches(isDisplayed()))
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
        try {
            onView(withText(eventName)).check(matches(isDisplayed()))
            Assert.fail("This event should have been deleted")
        }
        catch (_: AssertionError) {}
        catch (_: NoMatchingViewException) {}
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_edit_event() {
        val eventName = "Training Session"
        val newName = "Weekly Training Session"
        this.addEvent(eventName)

        onView(withId(R.id.schedule_events_view))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(eventName)), longClick()))
        onView(withText(R.string.bt_edit)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())

        onView(withId(R.id.schedule_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))

        this.removeEvent(newName)
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

        try {
            onView(withText(eventName)).check(matches(isDisplayed()))
            Assert.fail("This event should have been filtered out")
        } catch (_: AssertionError) {}

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_search_view))
            .perform(AndroidTestsUtil.clearText(), typeText(searchValue), closeSoftKeyboard())

        this.removeEvent(eventName)
    }
    // Condition - there is only one event with these properties
    @Test
    fun schedule_fragment_calendar_mode_add_remove_event() {
        onView(withId(R.id.schedule_switch_view)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        val eventName = "New Event"

        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withText(LocalDate.now().toString())).check(matches(isDisplayed()))

        onView(withId(R.id.schedule_et_name)).perform(typeText(eventName))
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.schedule_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(eventName)).check(matches(isDisplayed()))

        this.removeEvent(eventName)
        try {
            onView(withText(eventName)).check(matches(isDisplayed()))
            Assert.fail("This event should have been deleted")
        }
        catch (_: AssertionError) {}
        catch (_: NoMatchingViewException) {}
    }
    @Test
    fun schedule_fragment_calendar_mode_filter_event() {
        onView(withId(R.id.schedule_switch_view)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        val eventName = "New Event Today"
        this.addEvent(eventName)
        val todayDate = LocalDate.now()
        val nextDay = todayDate.plusDays(1).dayOfMonth

        onView(withIndex(withText(nextDay.toString()), 0)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        try {
            onView(withText(eventName)).check(matches(isDisplayed()))
            Assert.fail("It should have been filtered out")
        } catch (_: AssertionError) {}

        onView(withIndex(withText(todayDate.dayOfMonth.toString()), 0)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        this.removeEvent(eventName)
    }
    private fun addEvent(name: String) {
        onView(withId(R.id.schedule_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.schedule_et_name)).perform(typeText(name))
        onView(withId(R.id.schedule_et_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.schedule_et_time)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.schedule_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(name)).check(matches(isDisplayed()))
    }
    private fun removeEvent(name: String) {
        onView(withId(R.id.schedule_events_view))
            .perform(
                RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
}