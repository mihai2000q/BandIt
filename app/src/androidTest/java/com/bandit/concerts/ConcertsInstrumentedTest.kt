package com.bandit.concerts

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.util.AndroidTestUtil
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.AndroidTestUtil.withIndex
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ConcertsInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.navigation_concerts)).perform(click())
    }
    // Precondition - have a setup account with a band
    @Test
    fun concerts_fragment_ui() {
        onView(withId(R.id.concerts_bt_options)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.concerts_bt_filter)).check(matches(not(isDisplayed())))
        onView(withId(R.id.concerts_search_view)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_rv_band_empty)).check(matches(not(isDisplayed())))
        try {
            // if there is a concert, then check this
            onView(withId(R.id.concerts_rv_list)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.concerts_rv_empty)).check(matches(isDisplayed()))
        } catch (_: IncompatibleClassChangeError) {
            onView(withId(R.id.concerts_rv_empty)).check(matches(isDisplayed()))
        }

        onView(withId(R.id.concerts_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.concerts_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_filter)).check(matches(isDisplayed()))
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_add_remove_concert() {
        val concertName = "Concert in Romania"
        val concertCity = "Bucharest"
        val concertCountry = "Romania"
        this.addConcert(concertName, concertCity, concertCountry)
        this.removeConcert(concertName)
        AndroidTestUtil.checkIfItIsNotDisplayed(concertName,
            "This concert should have been deleted")
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_edit_concert() {
        val concertName = "Concert in Berlin"
        val concertCity = "Berlin"
        val concertCountry = "Germany"
        this.addConcert(concertName, concertCity, concertCountry)

        val newName = "Concert in Leipzig"
        val newCity = "Leipzig"
        this.editConcert(concertName, newName, newCity, concertCity)

        this.removeConcert(newName)
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_edit_remove_concert_swipe_gestures() {
        val concertName = "Romexpo Concert"
        val concertCity = "Vienna"
        val concertCountry = "Austria"
        val newName = "Concert in Austria"
        this.addConcert(concertName, concertCity, concertCountry)

        // edit concert
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.scrollTo<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(concertName))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(concertName)), swipeRight()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())

        onView(withId(R.id.concert_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(concertName,
            "This concert should have been renamed")

        // remove concert
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.scrollTo<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(newName))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(newName)), swipeLeft()))
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(newName,
            "This concert should have been deleted")
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_search_view_filter() {
        val concertName = "Concert To Be Filtered"
        val searchValue = "Concert To Be"
        this.addConcert(concertName)
        onView(withId(R.id.concerts_search_view))
            .perform(typeText(searchValue), closeSoftKeyboard())

        onView(withText(concertName)).check(matches(isDisplayed()))

        onView(withId(R.id.concerts_search_view))
            .perform(typeText("2"), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(concertName,
            "This concert should have been filtered out")

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concerts_search_view))
            .perform(AndroidTestUtil.clearText(), typeText(searchValue), closeSoftKeyboard())

        this.removeConcert(concertName)
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_filter_button() {
        val concertName = "Concert To Be Filtered"
        val searchValue = "Concert To Be"
        this.addConcert(concertName)
        // filter out the concert
        onView(withId(R.id.concerts_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.concerts_bt_filter)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(typeText("2323"), closeSoftKeyboard())
        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        AndroidTestUtil.checkIfItIsNotDisplayed(concertName,
            "This concert should have been filtered out")

        // filter for the concert to be seen
        onView(withId(R.id.concerts_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.concerts_bt_filter)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(searchValue), closeSoftKeyboard())
        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withText(concertName)).check(matches(isDisplayed()))

        this.removeConcert(concertName)
    }
    // Condition - there is only one concert and event with these properties
    @Test
    fun concerts_fragment_manipulate_concert_linked_to_events() {
        val name = "Awesome Concert"
        val city = "Rome"
        val country = "Italy"
        this.addConcert(name, city, country)
        onView(withId(R.id.navigation_schedule)).perform(click())
        onView(withText(name)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_concerts)).perform(click())

        val newName = "Worst Concert"
        this.editConcert(name, newName, "Milano", "Italia")

        onView(withId(R.id.navigation_schedule)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(name, "This event should have been edited")
        onView(withText(newName)).check(matches(isDisplayed()))

        onView(withId(R.id.navigation_concerts)).perform(click())
        this.removeConcert(newName)

        onView(withId(R.id.navigation_schedule)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(newName, "This event should have been removed")
    }
    // Condition - there is only one concert and event with these properties
    @Test
    fun concerts_fragment_detail_manipulate_concert() {
        val name = "A Concert Without Details"
        val newName = "An Edited Concert Without Details"
        this.addConcert(name)
        // edit the concert
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.scrollTo<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(name)), click()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.concert_detail_bt_edit)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.concert_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))

        // check inside the schedule if the concert has changed names
        onView(withId(R.id.navigation_schedule)).perform(click())
        onView(withText(newName)).check(matches(isDisplayed()))
        onView(withId(R.id.navigation_concerts)).perform(click())

        // delete the concert
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.scrollTo<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(newName))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(newName)), click()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withId(R.id.concert_detail_bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(newName,
            "This concert should have been removed")

        // check inside the schedule if the concert has been removed
        onView(withId(R.id.navigation_schedule)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(newName,
            "This event should have been removed")
    }
    private fun addConcert(
        name: String,
        city: String = "",
        country: String = ""
    ) {
        onView(withId(R.id.concerts_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.concerts_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name)).perform(typeText(name), closeSoftKeyboard())
        onView(withId(R.id.concert_et_date)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.concert_et_time)).perform(click())
        onView(withText("OK")).perform(click())

        if(city.isNotBlank())
            onView(withId(R.id.concert_et_city)).perform(typeText(city), closeSoftKeyboard())
        if(country.isNotBlank())
            onView(withId(R.id.concert_et_country)).perform(typeText(country), closeSoftKeyboard())

        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(name)).check(matches(isDisplayed()))
        if(country.isNotBlank())
            onView(withIndex(withText(country), 0)).check(matches(isDisplayed()))
        if(city.isNotBlank())
            onView(withIndex(withText(city),0)).check(matches(isDisplayed()))
    }
    private fun removeConcert(name: String) {
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(name)), longClick()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
    private fun editConcert(
        oldName: String,
        newName: String,
        newCity: String,
        newCountry: String
    ) {
        onView(withId(R.id.concerts_rv_list))
            .perform(RecyclerViewActions.scrollTo<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(oldName))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(oldName)), longClick()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withText(R.string.bt_edit)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.concert_et_city))
            .perform(clearText(), typeText(newCity), closeSoftKeyboard())
        onView(withId(R.id.concert_et_country))
            .perform(clearText(), typeText(newCountry), closeSoftKeyboard())

        onView(withId(R.id.concert_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))
        onView(withText(newCountry)).check(matches(isDisplayed()))
        onView(withText(newCity)).check(matches(isDisplayed()))
    }
}