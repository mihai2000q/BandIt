package com.bandit.home

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    private fun beforeEach() {
        //go to home
        onView(withId(R.id.login_bt_login)).perform(click())
    }
    @Test
    fun home_elements() {
        beforeEach()
        //header
        onView(withId(R.id.home_switch_modes)).check(matches(isDisplayed()))
        onView(withId(R.id.home_tv_welcome)).check(matches(isDisplayed()))
        onView(withId(R.id.home_tv_title)).check(matches(withText("BandIt")))
        onView(withId(R.id.home_bt_account)).check(matches(isDisplayed()))
        onView(withId(R.id.home_bt_band)).check(matches(isDisplayed()))

        onView(withId(R.id.home_scroll_view)).check(matches(isDisplayed()))
        onView(withId(R.id.home_sv_table_layout)).check(matches(isDisplayed()))
        onView(withText("Your Concerts")).check(matches(isDisplayed()))
        onView(withText("Your Songs")).check(matches(isDisplayed()))
        onView(withText("Your Chats")).check(matches(isDisplayed()))
        onView(withText("Your Schedule")).check(matches(isDisplayed()))
    }
    @Test
    fun home_elements_navigation_concerts() {
        beforeEach()
        onView(withText("Your Concerts")).perform(click())
        onView(withId(R.id.concerts_tv_title)).check(matches(withText("Concerts")))
        onView(withId(R.id.navigation_concerts)).check(matches(isSelected()))
    }
    @Test
    fun home_elements_navigation_songs() {
        beforeEach()
        onView(withText("Your Songs")).perform(click())
        onView(withText("This is the Songs Fragment")).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_songs)).check(matches(isSelected()))
    }
    @Test
    fun home_elements_navigation_chats() {
        beforeEach()
        onView(withText("Your Chats")).perform(click())
        onView(withText("This is the Chats Fragment")).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_social)).check(matches(isSelected()))
    }
    @Test
    fun home_elements_navigation_schedule() {
        beforeEach()
        onView(withText("Your Schedule")).perform(click())
        onView(withText("This is the Schedule Fragment")).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_schedule)).check(matches(isSelected()))
    }
    @Test
    fun home_account_dialog() {
        beforeEach()
        onView(withId(R.id.home_bt_account)).perform(click())
        onView(withText("This is the Account Dialog Fragment")).check(matches(isDisplayed()))
    }
}