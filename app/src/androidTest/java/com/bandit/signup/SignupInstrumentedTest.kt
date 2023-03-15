package com.bandit.signup

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestUtil.getResourceString
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class SignupInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        onView(withId(R.id.login_bt_signup)).perform(click())
    }
    @Test
    fun signup_fragment_ui() {
        onView(withId(R.id.main_bottom_navigation_view)).check(matches(Matchers.not(isDisplayed())))
        onView(withId(R.id.signup_bt_signup)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_bt_go_back)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_et_email)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_et_password)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_tv_email)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_tv_password)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_iv_logo)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_cb_terms)).check(matches(isNotChecked()))
    }
    @Test
    fun signup_fragment_validation() {
        // validation empty email field
        onView(withId(R.id.signup_bt_signup)).perform(click())
        onView(withText(R.string.et_email_validation_empty)).check(matches(isDisplayed()))

        // validation for email pattern
        onView(withId(R.id.signup_et_email))
            .perform(typeText("Not An Email"), closeSoftKeyboard())
        onView(withId(R.id.signup_bt_signup)).perform(click())
        onView(withText(R.string.et_email_validation_email)).check(matches(isDisplayed()))

        // validation empty password field
        onView(withId(R.id.signup_et_email))
            .perform(clearText(), typeText(ConstantsTest.adminEmail), closeSoftKeyboard())
        onView(withId(R.id.signup_bt_signup)).perform(click())
        onView(withText(R.string.et_pass_validation_empty)).check(matches(isDisplayed()))

        // validation minimum password
        onView(withId(R.id.signup_et_password))
            .perform(typeText("1234567"), pressImeActionButton())
        onView(withText(R.string.et_pass_validation_minimum)).check(matches(isDisplayed()))

        // validation terms and conditions
        onView(withId(R.id.signup_et_password))
            .perform(clearText(), typeText("123456789"), pressImeActionButton())
        onView(withText(R.string.cb_terms_validation)).check(matches(isDisplayed()))

        // validation email already being used
        onView(withId(R.id.signup_cb_terms)).perform(click())
        onView(withId(R.id.signup_et_password))
            .perform(clearText(), typeText("123456789"), pressImeActionButton())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen / 3))
        onView(withText(R.string.et_email_validation_email_already_used)).check(matches(isDisplayed()))
    }
    // Condition - there should not be another account with the same email
    @Test
    fun signup_fragment_create_account_and_login() {
        val userEmail = ConstantsTest.newUserEmail
        val userPassword = ConstantsTest.newUserPassword
        onView(withId(R.id.signup_et_email)).perform(typeText(userEmail))
        onView(withId(R.id.signup_et_password))
            .perform(typeText(userPassword), closeSoftKeyboard())
        onView(withId(R.id.signup_cb_terms)).perform(click())
        onView(withId(R.id.signup_bt_signup)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen / 2))

        onView(withId(R.id.signup_bt_go_back)).perform(click())

        // test to see if it has been created successfully
        TestUtil.login(userEmail, userPassword)
        onView(withId(R.id.main_toolbar))
            .check(matches(hasDescendant(withText(R.string.first_login_label))))
    }
}