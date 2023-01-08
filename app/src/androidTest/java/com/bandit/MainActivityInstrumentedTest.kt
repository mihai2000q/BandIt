package com.bandit

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Test
    fun main_activity_on_start() {
        onView(ViewMatchers.withId(R.id.main_nav_host)).check(matches(isDisplayed()))
    }
}