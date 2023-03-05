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
        onView(withId(R.id.concerts_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_bt_filter)).check(matches(isDisplayed()))
        onView(withId(R.id.concerts_search_view)).check(matches(isDisplayed()))
        try {
            // if there is a concert, then check this
            onView(withId(R.id.concerts_list)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.concerts_rv_empty)).check(matches(isDisplayed()))
        }
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_add_remove_concert() {
        val concertName = "Concert in Romania"
        val concertCity = "Bucharest"
        val concertCountry = "Romania"
        val concertPlace = "Rom Arena"
        this.addConcert(concertName, concertCity, concertCountry, concertPlace)
        this.removeConcert(concertName)
        AndroidTestUtil.checkIfItIsNotDisplayed(withText(concertName),
            "This concert should have been deleted")
    }
    // Condition - there is only one concert with these properties
    @Test
    fun concerts_fragment_edit_concert() {
        val concertName = "Concert in Berlin"
        val concertCity = "Berlin"
        val concertCountry = "Germany"
        val concertPlace = "Berlin Arena"
        this.addConcert(concertName, concertCity, concertCountry, concertPlace)

        val newName = "Concert in Leipzig"
        val newCity = "Leipzig"
        val newPlace = "Red Bull Arena"

        onView(withId(R.id.concerts_list))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(concertName)), longClick()))
        onView(withText(R.string.bt_edit)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.concert_et_city))
            .perform(clearText(), typeText(newCity), closeSoftKeyboard())
        onView(withId(R.id.concert_et_place))
            .perform(clearText(), typeText(newPlace), closeSoftKeyboard())

        onView(withId(R.id.concert_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(newName)).check(matches(isDisplayed()))
        onView(withText("$newCity, $concertCountry")).check(matches(isDisplayed()))
        onView(withText(newPlace)).check(matches(isDisplayed()))

        this.removeConcert(newName)
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

        AndroidTestUtil.checkIfItIsNotDisplayed(withText(concertName),
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
        onView(withId(R.id.concerts_bt_filter)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(typeText("2323"), closeSoftKeyboard())
        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        AndroidTestUtil.checkIfItIsNotDisplayed(withText(concertName),
            "This concert should have been filtered out")

        // filter for the concert to be seen
        onView(withId(R.id.concerts_bt_filter)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name))
            .perform(clearText(), typeText(searchValue), closeSoftKeyboard())
        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withText(concertName)).check(matches(isDisplayed()))

        this.removeConcert(concertName)
    }
    private fun addConcert(
        name: String,
        city: String = "",
        country: String = "",
        place: String = ""
    ) {
        onView(withId(R.id.concerts_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.concert_et_name)).perform(typeText(name))
        onView(withId(R.id.concert_et_date)).perform(click())
        onView(withText("OK")).perform(click())

        onView(withId(R.id.concert_et_time)).perform(click())
        onView(withText("OK")).perform(click())

        if(city.isNotBlank())
            onView(withId(R.id.concert_et_city)).perform(typeText(city))
        if(country.isNotBlank())
            onView(withId(R.id.concert_et_country)).perform(typeText(country))
        if(place.isNotBlank())
            onView(withId(R.id.concert_et_place)).perform(typeText(place), closeSoftKeyboard())

        onView(withId(R.id.concert_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(name)).check(matches(isDisplayed()))
        if(city.isNotBlank() && country.isNotBlank())
            onView(withText("$city, $country")).check(matches(isDisplayed()))
        if(place.isNotBlank())
            onView(withText(place)).check(matches(isDisplayed()))
    }
    private fun removeConcert(name: String) {
        onView(withId(R.id.concerts_list))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
}