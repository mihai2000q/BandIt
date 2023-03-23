package com.bandit.friends

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.util.AndroidTestUtil
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class FriendsInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    private fun beforeEach(email: String, password: String) {
        TestUtil.login(email, password)
        onView(withId(R.id.navigation_social)).perform(click())
        onView(withText(R.string.social_friends_tab)).perform(click())
    }
    // Precondition for all tests - have a setup account that is not friends with the Admin yet
    // The test will take the new user from the Sign Up/ First Login and Account tests
    // and will befriend the Admin
    @Test
    fun friends_fragment_ui() {
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        onView(withId(R.id.friends_bt_options)).check(matches(isDisplayed()))
        onView(withId(R.id.friends_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.friends_bt_requests)).check(matches(not(isDisplayed())))
        onView(withId(R.id.friends_search_view)).check(matches(isDisplayed()))
        try {
            onView(withId(R.id.friends_rv_list)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            onView(withId(R.id.friends_rv_empty)).check(matches(isDisplayed()))
        } catch (_: IncompatibleClassChangeError) {
            onView(withId(R.id.friends_rv_empty)).check(matches(isDisplayed()))
        }
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.friends_bt_requests)).check(matches(isDisplayed()))
    }
    // Condition - the user is not friends with the admin
    // the new user sends a friend request to the admin
    @Test
    fun friends_fragment_send_friend_request() {
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        val accountToBefriend = "Admin"
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())

        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // make sure that the admin account is not displayed anymore on the list
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed anymore in add friends")
    }
    // Precondition - the user sent the admin a friend request
    // the admin rejects the friend request
    @Test
    fun friends_fragment_reject_friend_request() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        val accountToBefriend = ConstantsTest.accountNewName
        // check if the other account is still there
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")
        onView(isRoot()).perform(pressBack())

        // reject the request
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))
        onView(withId(R.id.friend_rq_bt_reject)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friends fragment")

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friend requests")

        onView(isRoot()).perform(pressBack())

        // the account should be displayed in add friends
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
    }
    // same as above
    @Test
    fun friends_fragment_reject_friend_request_swipe() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        val accountToBefriend = ConstantsTest.accountNewName
        // check if the other account is still there
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")
        onView(isRoot()).perform(pressBack())

        // reject the request
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))
        onView(withText(accountToBefriend)).perform(swipeLeft())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friends fragment")

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friend requests")

        onView(isRoot()).perform(pressBack())

        // the account should be displayed in add friends
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
    }
    // Precondition - the user sent the admin a friend request
    // the admin accepts the friend request
    @Test
    fun friends_fragment_accept_friend_request() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        val accountToBefriend = ConstantsTest.accountNewName
        // check if the other account is still there
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")

        onView(isRoot()).perform(pressBack())

        // accept the request
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))
        onView(withId(R.id.friend_rq_bt_accept)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friend requests")
    }
    @Test
    fun friends_fragment_accept_friend_request_swipe() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        val accountToBefriend = ConstantsTest.accountNewName
        // check if the other account is still there
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")

        onView(isRoot()).perform(pressBack())

        // accept the request
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))
        onView(withText(accountToBefriend)).perform(swipeRight())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        onView(withText(accountToBefriend)).check(matches(isDisplayed()))

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in add friends")
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_requests)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToBefriend.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToBefriend,
            "This account should not be displayed in friend requests")
    }
    // Precondition - the user must be friends with the admin
    // the user unfriends the admin
    @Test
    fun friends_fragment_unfriend() {
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        val accountToUnfriend = "Admin"
        onView(withId(R.id.friends_search_view))
            .perform(typeText(accountToUnfriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToUnfriend)).perform(longClick())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withText(R.string.bt_unfriend)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToUnfriend,
            "This account should not be displayed in friends fragment")

        // check if it is displayed again in people
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToUnfriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToUnfriend)).check(matches(isDisplayed()))
    }
    // same as above
    @Test
    fun friends_fragment_unfriend_swipe() {
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        val accountToUnfriend = "Admin"
        onView(withId(R.id.friends_search_view))
            .perform(typeText(accountToUnfriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToUnfriend)).perform(swipeLeft())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(accountToUnfriend,
            "This account should not be displayed in friends fragment")

        // check if it is displayed again in people
        onView(withId(R.id.friends_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.friends_bt_add)).perform(click())
        onView(withId(R.id.friends_dialog_search_view))
            .perform(typeText(accountToUnfriend.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToUnfriend)).check(matches(isDisplayed()))
    }
}