package com.bandit.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.util.AndroidTestUtil
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.ConstantsTest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        onView(withId(R.id.login_et_email)).perform(clearText())
    }
    @Test
    fun login_fragment_ui() {
        onView(withId(R.id.main_bottom_navigation_view)).check(matches(Matchers.not(isDisplayed())))
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.login_label))))
        onView(withId(R.id.login_et_email)).check(matches(isDisplayed()))
        onView(withId(R.id.login_et_password)).check(matches(isDisplayed()))
        onView(withId(R.id.login_bt_login)).check(matches(withText(R.string.bt_login)))
        onView(withId(R.id.login_bt_signup)).check(matches(withText(R.string.bt_sign_up)))
        onView(withId(R.id.login_cb_remember)).check(matches(isNotChecked()))
    }
    @Test
    fun login_fragment_log_in_validation() {
        val randomEmail = "rando@random.com"
        // validation empty email field
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withText(R.string.et_email_validation_empty)).check(matches(isDisplayed()))

        // validation for email pattern
        onView(withId(R.id.login_et_email))
            .perform(typeText("something"), closeSoftKeyboard())
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withText(R.string.et_email_validation_email)).check(matches(isDisplayed()))

        // validation empty password field
        onView(withId(R.id.login_et_email))
            .perform(clearText(), typeText(randomEmail), closeSoftKeyboard())
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withText(R.string.et_pass_validation_empty)).check(matches(isDisplayed()))

        // validation minimum password
        onView(withId(R.id.login_et_password)).perform(typeText("1234567"))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(withText(R.string.et_pass_validation_minimum)).check(matches(isDisplayed()))

        // validation email not used
        onView(withId(R.id.login_et_password))
            .perform(clearText(), typeText("123456789"))
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen / 3))
        onView(withId(R.id.login_et_email)).perform(click())
        onView(withText(R.string.et_email_validation_email_already_used)).check(matches(isDisplayed()))

        // validation incorrect password
        onView(withId(R.id.login_et_email))
            .perform(clearText(), typeText(ConstantsTest.adminEmail))
        onView(withId(R.id.login_et_password))
            .perform(clearText(), typeText("123456789"), closeSoftKeyboard())
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen / 3))
        onView(withText(R.string.et_pass_validation_incorrect)).check(matches(isDisplayed()))
    }
    @Test
    fun login_fragment_login() {
        onView(withId(R.id.login_et_email)).perform(typeText(ConstantsTest.adminEmail))
        onView(withId(R.id.login_et_password))
            .perform(typeText(ConstantsTest.adminPassword), closeSoftKeyboard())
        onView(withId(R.id.login_bt_login)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen))
        // check if it is the home tab
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.home_label))))
    }
    @Test
    fun login_fragment_navigation_to_signup() {
        onView(withId(R.id.login_bt_signup)).perform(click())
        onView(withId(R.id.main_toolbar)).check(matches(hasDescendant(withText(R.string.signup_label))))
    }
}