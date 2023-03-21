package com.bandit.first.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FirstLoginInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
    }
    @Test
    fun first_login_fragment_ui() {
        onView(withId(R.id.main_bottom_navigation_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.first_login_image_flipper)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_bt_next)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_bt_previous)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_et_name)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_et_nickname)).check(matches(not(isDisplayed())))
        onView(withId(R.id.first_login_spinner_role)).check(matches(not(isDisplayed())))
        onView(withId(R.id.first_login_profile_picture)).check(matches(not(isDisplayed())))
        onView(withId(R.id.first_login_tv_congrats)).check(matches(not(isDisplayed())))
    }
    // Precondition - needs an account to be signed up (can take one from the Sign Up tests)
    @Test
    fun first_login_fragment_navigate_back_to_login() {
        onView(withId(R.id.main_toolbar))
            .check(matches(hasDescendant(withText(R.string.first_login_label))))
        onView(withId(R.id.first_login_bt_next)).check(matches(withText(R.string.bt_next)))
        onView(withId(R.id.first_login_image_flipper)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_progress_bar)).check(matches(isDisplayed()))

        onView(withId(R.id.first_login_bt_previous)).perform(click())

        onView(withId(R.id.main_toolbar))
            .check(matches(hasDescendant(withText(R.string.login_label))))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
    }
    // Precondition - needs an account to be signed up (can take one from the Sign Up tests)
    // This test also tests the validation part
    @Test
    fun first_login_fragment_setup_account() {
        val name = ConstantsTest.accountName
        val nickname = ConstantsTest.accountNickname
        // first phase - choose the name
        onView(withId(R.id.first_login_bt_next)).perform(click())
        onView(withText(R.string.et_name_validation)).check(matches(isDisplayed()))

        onView(withId(R.id.first_login_et_name))
            .perform(typeText(name), pressImeActionButton())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        // second phase - choose the nickname
        onView(withId(R.id.first_login_bt_next)).perform(click())
        onView(withText(R.string.et_nickname_validation)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_et_nickname))
            .perform(typeText(nickname), pressImeActionButton())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        // third phase - choose the role
        onView(withId(R.id.first_login_spinner_role)).perform(click())
        onView(withText(ConstantsTest.accountRole)).perform(click())
        onView(withId(R.id.first_login_bt_next)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        // last phase - choose the profile picture
        onView(withId(R.id.first_login_profile_picture)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_bt_next)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        // congratulations
        onView(withId(R.id.first_login_tv_congrats)).check(matches(isDisplayed()))
        onView(withId(R.id.first_login_bt_previous)).check(matches(not(isDisplayed())))
        onView(withId(R.id.first_login_bt_next)).check(matches(withText(R.string.first_login_bt_next_last)))
        onView(withId(R.id.first_login_bt_next)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen))

        onView(withId(R.id.main_bottom_navigation_view)).check(matches((isDisplayed())))
        onView(withId(R.id.main_toolbar))
            .check(matches(hasDescendant(withText(R.string.home_label))))
    }
}