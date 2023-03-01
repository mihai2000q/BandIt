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

    }
    @Test
    fun home_elements_navigation_concerts() {

    }
    @Test
    fun home_elements_navigation_songs() {

    }
    @Test
    fun home_elements_navigation_chats() {

    }
    @Test
    fun home_elements_navigation_schedule() {

    }
    @Test
    fun home_account_dialog() {

    }
}