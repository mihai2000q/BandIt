package com.bandit.account

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestsUtil.waitFor
import com.bandit.util.AndroidTestsUtil.withIndex
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class AccountInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        onView(withId(R.id.action_bar_profile)).perform(click())
    }
    // Condition - needs an account already setup with these exact name/nickname and role
    @Test
    fun account_fragment_ui() {
        onView(withId(R.id.account_iv_profile_picture)).check(matches(isDisplayed()))
        onView(withId(R.id.account_tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.account_tv_nickname)).check(matches(isDisplayed()))
        onView(withId(R.id.account_tv_role)).check(matches(isDisplayed()))
        onView(withId(R.id.account_spinner_role)).check(matches(isDisplayed()))
        onView(withId(R.id.account_et_name)).check(matches(withText(ConstantsTest.accountName)))
        onView(withId(R.id.account_et_nickname)).check(matches(withText(ConstantsTest.accountNickname)))
        onView(withText(ConstantsTest.accountRole)).check(matches(isDisplayed()))
        onView(withId(R.id.account_bt_sign_out)).check(matches(withText(R.string.bt_sign_out)))
        onView(withId(R.id.account_bt_save)).check(matches(withText(R.string.bt_save)))
    }
    // Condition - needs an account already setup
    @Test
    fun account_fragment_update_account() {
        val newName = "Best Tester"
        val newNickname = "Bester"

        onView(withId(R.id.account_iv_profile_picture)).perform(click())
        onView(withId(R.id.image_picker_tv_header)).check(matches(isDisplayed()))

        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.account_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.account_et_nickname))
            .perform(clearText(), typeText(newNickname), closeSoftKeyboard())
        onView(withId(R.id.account_bt_save)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // check if changes were made
        onView(withId(R.id.account_et_name)).check(matches(withText(newName)))
        onView(withId(R.id.account_et_nickname)).check(matches(withText(newNickname)))

        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.action_bar_profile)).perform(click())

        // check if changes are still valid after refreshing
        onView(withId(R.id.account_et_name)).check(matches(withText(newName)))
        onView(withId(R.id.account_et_nickname)).check(matches(withText(newNickname)))
    }
    @Test
    fun account_fragment_sign_out() {
        onView(withId(R.id.account_bt_sign_out)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.main_toolbar))
            .check(matches(hasDescendant(withText(R.string.login_label))))

        onView(withIndex(withText(ConstantsTest.newUserEmail), 0))
            .check(matches(isDisplayed()))
    }
}