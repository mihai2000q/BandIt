package com.bandit.band

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.util.AndroidTestUtil
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.BandMemberAdapter
import com.bandit.ui.adapter.PeopleAdapter
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class BandInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    // Precondition - have an account setup already
    private fun beforeEach(email: String, password: String) {
        TestUtil.login(email, password)
        onView(withId(R.id.navigation_social)).perform(click())
        onView(withText(R.string.social_band_tab)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
    }
    // Precondition - needs an account that is in a band
    @Test
    fun band_fragment_ui_with_band() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_rv_member_list)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_options)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_abandon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_invitations)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_tv_name)).check(matches(isDisplayed()))
        onView(withId(R.id.band_search_view)).check(matches(isDisplayed()))

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_abandon)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_invitations)).check(matches(isDisplayed()))

        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))

        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.account_et_band_name)).check(matches(withText(ConstantsTest.bandName)))
    }
    // Precondition - needs an account that has no band
    @Test
    fun band_fragment_ui_without_band() {
        beforeEach(ConstantsTest.noBandEmail, ConstantsTest.noBandPassword)
        this.without_band_ui()
    }
    // Precondition - needs an account that has no band
    // the admin creates a band
    @Test
    fun band_fragment_create_band() {
        val bandName = ConstantsTest.bandName
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_bt_create)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.create_band_et_name))
            .perform(typeText(bandName), pressImeActionButton())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withId(R.id.band_bt_create)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_abandon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_invitations)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_tv_name)).check(matches(withText(bandName)))

        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_you)).check(matches(isDisplayed()))

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_abandon)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_invitations)).check(matches(isDisplayed()))

        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(withId(R.id.account_et_band_name)).check(matches(withText(bandName)))
    }
    // Precondition - have an account setup already with a band and you are the creator
    // the admin disbands the band
    @Test
    fun band_fragment_disband_band() {
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_abandon)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.bandName,
        "This text should not be visible anymore")
        this.without_band_ui()
    }
    // Precondition - have an account setup already with a band and a friend
    // the admin invites the new user to the band
    @Test
    fun band_fragment_invite_member() {
        val accountToInvite = ConstantsTest.accountNewName
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        // look for him and invite him
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_add_member_search))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountToInvite)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // check if he is added as a pending member
        onView(withId(R.id.band_search_view))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        onView(withText(ConstantsTest.accountNewNickname)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_accepted_false)).check(matches(isDisplayed()))

        // check if he still is on the add members dialog
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_add_member_search))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToInvite,
        "This account should have been invited, and therefore, removed from this list")
    }
    // same as above
    @Test
    fun band_fragment_invite_member_from_friends() {
        val accountToInvite = ConstantsTest.accountNewName
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        // look for him and invite him
        onView(withText(R.string.social_friends_tab)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.friends_rv_list))
            .perform(
                RecyclerViewActions.scrollTo<PeopleAdapter.ViewHolder>(
                hasDescendant(withText(accountToInvite))),
                RecyclerViewActions.actionOnItem<PeopleAdapter.ViewHolder>(
                    hasDescendant(withText(accountToInvite)),
                    longClick()
                )
            )
        onView(withText(R.string.bt_add_member)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(R.string.social_band_tab)).perform(click())

        // check if he is added as a pending member
        onView(withId(R.id.band_search_view))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        onView(withText(ConstantsTest.accountNewNickname)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_accepted_false)).check(matches(isDisplayed()))

        // check if he still is on the add members dialog
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_add_member_search))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToInvite,
            "This account should have been invited, and therefore, removed from this list")
    }
    // same as above
    // it cannot swipe as it swipes the tab instead
    @Test
    @Ignore("fails on line 201")
    fun band_fragment_invite_member_from_friends_swipe() {
        val accountToInvite = ConstantsTest.accountNewName
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        // look for him and invite him
        onView(withText(R.string.social_friends_tab)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.friends_rv_list))
            .perform(RecyclerViewActions.scrollTo<PeopleAdapter.ViewHolder>(
                    hasDescendant(withText(accountToInvite))))
            .perform(RecyclerViewActions.actionOnItem<PeopleAdapter.ViewHolder>(
                    hasDescendant(withText(accountToInvite)), swipeRight()))

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(R.string.social_band_tab)).perform(click())

        // check if he is added as a pending member
        onView(withId(R.id.band_search_view))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        onView(withText(ConstantsTest.accountNewNickname)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_accepted_false)).check(matches(isDisplayed()))

        // check if he still is on the add members dialog
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_add_member_search))
            .perform(typeText(accountToInvite.dropLast(1)), closeSoftKeyboard())
        AndroidTestUtil.checkIfItIsNotDisplayed(accountToInvite,
            "This account should have been invited, and therefore, removed from this list")
    }
    // Precondition - have an account setup already with a band
    // Condition - Be invited in the above created band
    // the user accepts the admin's invitation
    @Test
    fun band_fragment_accept_band_invitation() {
        val bandName = ConstantsTest.bandName
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        // accept the invitation
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_invitation_search_view))
            .perform(typeText(bandName.dropLast(1)), closeSoftKeyboard())
        onView(withId(R.id.band_invitation_tv_band_name)).check(matches(withText(bandName)))
        onView(withId(R.id.band_invitation_bt_accept)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withId(R.id.band_tv_name)).check(matches(withText(bandName)))
        onView(withText(ConstantsTest.adminNickname)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))

        onView(withText(R.string.band_member_you)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_accepted_true)).check(matches(isDisplayed()))

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.adminNickname,
            "The band invitation has already been accepted")
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.adminNickname,
        "The admin's nickname should not be displayed as he cannot be invited in its own band")
        onView(isRoot()).perform(pressBack())

        // check my profile's band
        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.account_et_band_name)).check(matches(withText(bandName)))
    }
    // same as above
    @Test
    fun band_fragment_accept_band_invitation_swipe() {
        val bandName = ConstantsTest.bandName
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        // accept the invitation
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_invitation_search_view))
            .perform(typeText(bandName.dropLast(1)), closeSoftKeyboard())
        onView(withId(R.id.band_invitation_tv_band_name)).check(matches(withText(bandName)))
        onView(withText(bandName)).perform(swipeRight())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withId(R.id.band_tv_name)).check(matches(withText(bandName)))
        onView(withText(ConstantsTest.adminNickname)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_creator)).check(matches(isDisplayed()))

        onView(withText(R.string.band_member_you)).check(matches(isDisplayed()))
        onView(withText(R.string.band_member_accepted_true)).check(matches(isDisplayed()))

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.adminNickname,
            "The band invitation has already been accepted")
        onView(isRoot()).perform(pressBack())

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.adminNickname,
            "The admin's nickname should not be displayed as he cannot be invited in its own band")
        onView(isRoot()).perform(pressBack())

        // check my profile's band
        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.account_et_band_name)).check(matches(withText(bandName)))
    }
    // Precondition - have an account setup already with a band
    // Condition - Be invited in the above created band
    // the user rejects the admin's invitation
    @Test
    fun band_fragment_reject_band_invitation() {
        val band = ConstantsTest.bandName
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(withId(R.id.band_invitation_search_view))
            .perform(typeText(band.dropLast(1)), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.band_invitation_tv_band_name)).check(matches(withText(band)))
        onView(withId(R.id.band_invitation_bt_reject)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(band,
        "The name of the band should not be displayed anywhere")
    }
    // same as above
    @Test
    fun band_fragment_reject_band_invitation_swipe() {
        val bandName = ConstantsTest.bandName
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).perform(click())
        onView(withId(R.id.band_invitation_search_view))
            .perform(typeText(bandName.dropLast(1)), closeSoftKeyboard())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.band_invitation_tv_band_name)).check(matches(withText(bandName)))
        onView(withText(bandName)).perform(swipeLeft())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(bandName,
            "The name of the band should not be displayed anywhere")
    }
    // Precondition - have an account setup already with a band and you are not the creator
    // the user quits the band
    @Test
    fun band_fragment_abandon_band() {
        beforeEach(ConstantsTest.newUserEmail, ConstantsTest.newUserPassword)
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_abandon)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(ConstantsTest.bandName,
        "The account should have abandoned the band")
        this.without_band_ui()
    }
    // Precondition - have an account setup already with a band and a member in it
    // the admin kicks the user from the band
    @Test
    fun band_fragment_kick_member() {
        val accountNickToKick = ConstantsTest.accountNewNickname
        val accountNameToKick = ConstantsTest.accountNewName
        beforeEach(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.band_rv_member_list))
            .perform(
                RecyclerViewActions.scrollTo<BandMemberAdapter.ViewHolder>(
                    hasDescendant(withText(accountNickToKick))),
                RecyclerViewActions.actionOnItem<BandMemberAdapter.ViewHolder>(
                    hasDescendant(withText(accountNickToKick)), longClick())
            )
        onView(withText(R.string.bt_kick_member)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // check if the member is still there
        AndroidTestUtil.checkIfItIsNotDisplayed(accountNickToKick,
        "This account should have been kicked")
        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.band_add_member_search))
            .perform(typeText(accountNameToKick.dropLast(1)), closeSoftKeyboard())
        onView(withText(accountNameToKick)).check(matches(isDisplayed()))
    }
    private fun without_band_ui() {
        onView(withId(R.id.band_bt_create)).check(matches(isDisplayed()))
        onView(withId(R.id.band_bt_options)).check(matches(isDisplayed()))

        onView(withId(R.id.band_bt_invitations)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_abandon)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_search_view)).check(matches(not(isDisplayed())))
        onView(withId(R.id.band_tv_name)).check(matches(not(isDisplayed())))

        onView(withId(R.id.band_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.band_bt_invitations)).check(matches(isDisplayed()))

        onView(withId(R.id.action_bar_profile)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.account_et_band_name)).check(matches(withText("")))
    }
}