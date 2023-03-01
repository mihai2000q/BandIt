package com.bandit.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.AndroidTestsUtil
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginFragmentInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        onView(withId(R.id.login_et_email)).perform(clearText())
    }
    @Test
    fun login_fragment_log_in_validation() {
        // validation empty email field
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withId(R.id.login_et_email))
            .check(matches(hasErrorText(AndroidTestsUtil.getResourceString(R.string.et_email_validation_empty))))

        // validation for email pattern
        onView(withId(R.id.login_et_email)).perform(typeText("something"))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withId(R.id.login_et_email))
            .check(matches(hasErrorText(AndroidTestsUtil.getResourceString(R.string.et_email_validation_email))))

        // validation empty password field
        onView(withId(R.id.login_et_email)).perform(clearText())
        onView(withId(R.id.login_et_email)).perform(typeText(AndroidTestsUtil.accountEmail))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withId(R.id.login_et_password))
            .check(matches(hasErrorText(AndroidTestsUtil.getResourceString(R.string.et_pass_validation_empty))))

        // validation minimum password field
        onView(withId(R.id.login_et_password)).perform(typeText("1234567"))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withId(R.id.login_et_password))
            .check(matches(hasErrorText(AndroidTestsUtil.getResourceString(R.string.et_pass_validation_minimum))))

        // validation incorrect password
        onView(withId(R.id.login_et_password)).perform(clearText())
        onView(withId(R.id.login_et_password)).perform(typeText("123456789"))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withId(R.id.login_et_password))
            .check(matches(hasErrorText(AndroidTestsUtil.getResourceString(R.string.et_pass_validation_incorrect))))
    }
    @Test
    fun login_fragment_login() {
        onView(withId(R.id.login_et_email)).perform(typeText(AndroidTestsUtil.accountEmail))
        onView(withId(R.id.login_et_password)).perform(typeText(AndroidTestsUtil.accountPassword))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withText(R.string.login_toast)).check(matches(isDisplayed()))
    }
}