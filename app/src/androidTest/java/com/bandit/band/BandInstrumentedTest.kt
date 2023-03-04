package com.bandit.band

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.util.AndroidTestsUtil
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BandInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    private fun beforeEach(email: String, password: String) {
        TestUtil.login(email, password)
        onView(withId(R.id.navigation_social)).perform(click())
        onView(withText(R.string.social_band_tab)).perform(click())
    }
    @Test
    fun band_fragment_ui_with_band() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_rv_member_list)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_abandon)).check(matches(isDisplayed()))
        onView(withId(R.id.band_tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_invitations)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_add)).check(matches(isDisplayed()))

        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))
    }
    @Test
    fun band_fragment_ui_without_band() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_bt_create)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_invitations)).check(matches(isDisplayed()))

        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(withId(R.id.account_tv_band_name)).check(matches(not(isDisplayed())))
    }
    @Test
    fun band_fragment_create_band() {
        val bandName = "Admin's Band"
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_bt_create)).perform(click())
        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.create_band_et_name)).perform(typeText(bandName), pressImeActionButton())

        onView(isRoot()).perform(AndroidTestsUtil.waitFor(ConstantsTest.maximumDelayOperations))

        onView(withId(R.id.band_bt_create)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_abandon)).check(matches(withText(R.string.bt_disband)))
        onView(withId(R.id.band_tv_name)).check(matches(withText(bandName)))

        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))

        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(withId(R.id.account_tv_band_name)).check(matches(withText(bandName)))
    }
    @Test
    fun band_fragment_disband_band() {

    }
    @Test
    fun band_fragment_abandon_band() {

    }
    @Test
    fun band_fragment_add_member() {

    }
    @Test
    fun band_fragment_kick_member() {

    }
    @Test
    fun band_fragment_accept_band_invitation() {

    }
    @Test
    fun band_fragment_accept_reject_invitation() {

    }
}