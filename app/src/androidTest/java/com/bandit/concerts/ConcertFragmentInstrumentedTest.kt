package com.bandit.concerts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ConcertFragmentInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    private fun beforeEach() {
        //go to home
        onView(withId(R.id.fragment_login_bt_login)).perform(click())
        //go to concerts
        onView(withText("Your Concerts")).perform(click())
    }
    @Test
    fun concert_elements() {
        beforeEach()
        //header
        onView(withId(R.id.concerts_switch_modes)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_tv_title)).check(matches(withText("Concerts")))
        onView(withId(R.id.concerts_bt_account)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_filter)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_replacement)).check(matches(isDisplayed()))

        onView(withId(R.id.concerts_list)).check(matches(isDisplayed()))
    }
    @Test
    fun concert_concert_list() {
        beforeEach()
        onView(withId((R.id.concerts_list)))
            .perform(actionOnItemAtPosition<ConcertAdapter.ViewHolder>(0, click()))
    }
}