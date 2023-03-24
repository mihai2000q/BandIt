package com.bandit.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.DrawerActions.open
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestUtil
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    // Precondition - have an account already setup
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
    }
    @Test
    fun home_fragment_user_interface() {
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.home_label))))
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withId(R.id.action_bar_profile))))
        onView(withText("Your Concerts")).check(matches(isDisplayed()))
        onView(withText("Your Songs")).check(matches(isDisplayed()))
        onView(withText("Your Social")).check(matches(isDisplayed()))
        onView(withText("Your Schedule")).check(matches(isDisplayed()))
        onView(withId(R.id.main_bottom_navigation_view)).check(matches(isDisplayed()))
        onView(withId(R.id.main_drawer_layout)).perform(open())
        onView(withId(R.id.main_drawer_menu)).check(matches(isDisplayed()))
    }
    @Test
    fun home_fragment_bottom_navigation_view() {
        onView(withId(R.id.navigation_concerts)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.concerts_label))))

        onView(withId(R.id.navigation_songs)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.songs_label))))

        onView(withId(R.id.navigation_social)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.social_label))))

        onView(withId(R.id.navigation_schedule)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.schedule_label))))

        onView(AndroidTestUtil.withIndex(withId(R.id.navigation_home), 1)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.home_label))))
    }
    @Test
    fun home_fragment_navigation_panel_concerts() {
        onView(withText("Your Concerts")).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.concerts_label))))
    }
    @Test
    fun home_fragment_navigation_panel_songs() {
        onView(withText("Your Songs")).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.songs_label))))
    }
    @Test
    fun home_fragment_navigation_panel_social() {
        onView(withText("Your Social")).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.social_label))))
    }
    @Test
    fun home_fragment_navigation_panel_schedule() {
        onView(withText("Your Schedule")).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.schedule_label))))
    }
    @Test
    fun home_fragment_drawer_menu() {
        onView(withId(R.id.main_bottom_navigation_view)).check(matches(isDisplayed()))

        onView(withId(R.id.main_drawer_layout)).perform(open())
        onView(withId(R.id.navigation_todolist)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.todolist_label))))

        onView(withId(R.id.main_bottom_navigation_view)).check(matches(not(isDisplayed())))

        onView(withId(R.id.main_drawer_layout)).perform(open())
        onView(withId(R.id.navigation_personal_notes)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.personal_notes_label))))

        onView(withId(R.id.main_drawer_layout)).perform(open())
        onView(AndroidTestUtil.withIndex(withId(R.id.navigation_home), 0)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.home_label))))

        onView(withId(R.id.main_bottom_navigation_view)).check(matches(isDisplayed()))
    }
}