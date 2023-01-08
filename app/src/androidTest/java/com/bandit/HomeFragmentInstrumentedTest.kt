package com.bandit

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeFragmentInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun home_elements() {
        onView(withId(R.id.fragment_login_bt_login)).perform(click())

        onView(withId(R.id.home_switch_modes)).check(matches(isDisplayed()))
        onView(withId(R.id.home_tv_welcome)).check(matches(isDisplayed()))
        onView(withId(R.id.home_tv_title)).check(matches(isDisplayed()))
        onView(withId(R.id.home_bt_account)).check(matches(isDisplayed()))
        onView(withId(R.id.home_bt_account)).check(matches(isDisplayed()))
        onView(withId(R.id.home_bt_replacement)).check(matches(isDisplayed()))
        onView(withId(R.id.home_scroll_view)).check(matches(isDisplayed()))
        onView(withId(R.id.home_sv_table_layout)).check(matches(isDisplayed()))
        onView(withText("Your Concerts")).check(matches(isDisplayed()))
        onView(withText("Your Songs")).check(matches(isDisplayed()))
        onView(withText("Your Chats")).check(matches(isDisplayed()))
        onView(withText("Your Schedule")).check(matches(isDisplayed()))
    }
    @Test
    fun home_elements_navigation_concerts() {
        //go to home
        onView(withId(R.id.fragment_login_bt_login)).perform(click())

        onView(withText("Your Concerts")).perform(click())
        onView(withId(R.id.concerts_tv_title)).check(matches(isDisplayed()))
    }
}