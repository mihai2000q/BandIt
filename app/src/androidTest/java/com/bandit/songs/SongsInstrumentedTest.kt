package com.bandit.songs

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.bandit.MainActivity
import com.bandit.R
import com.bandit.ui.adapter.AlbumAdapter
import com.bandit.ui.adapter.ConcertAdapter
import com.bandit.ui.adapter.SongAdapter
import com.bandit.util.AndroidTestUtil
import com.bandit.util.AndroidTestUtil.waitFor
import com.bandit.util.AndroidTestUtil.withIndex
import com.bandit.util.ConstantsTest
import com.bandit.util.TestUtil
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SongsInstrumentedTest {
    @get:Rule
    var activityTestRule = ActivityScenarioRule(MainActivity::class.java)
    // Precondition - have a setup account with a band already in place
    @Before
    fun setup() {
        TestUtil.login(ConstantsTest.adminEmail, ConstantsTest.adminPassword)
        onView(withId(R.id.navigation_songs)).perform(click())
    }
    @Test
    fun songs_fragment_ui() {
        // the songs mode and album share the same UI components
        // therefore, there is no need to check them as well
        onView(withId(R.id.songs_bt_options)).check(matches(isDisplayed()))
        onView(withId(R.id.songs_bt_add)).check(matches(not(isDisplayed())))
        onView(withId(R.id.songs_bt_filter)).check(matches(not(isDisplayed())))
        onView(withId(R.id.songs_bt_album_mode)).check(matches(not(isDisplayed())))
        onView(withId(R.id.songs_search_view)).check(matches(isDisplayed()))
        try {
            // if there is a concert, then check this
            onView(withId(R.id.songs_rv_list)).check(matches(isDisplayed()))
        } catch (_: AssertionError) {
            // if the above does not work, then check this
            onView(withId(R.id.songs_rv_empty)).check(matches(isDisplayed()))
        }

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_add)).check(matches(isDisplayed()))
        onView(withId(R.id.songs_bt_filter)).check(matches(isDisplayed()))
        onView(withId(R.id.songs_bt_album_mode)).check(matches(isDisplayed()))
    }
    // Condition - there is only one song with these properties
    @Test
    fun songs_fragment_add_remove_song() {
        val songName = "The Warrior"
        this.addSong(songName)
        this.removeSong(songName)
        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
        "This song should have been deleted")
    }
    // Condition - there is only one song with these properties
    @Test
    fun songs_fragment_edit_song() {
        val songName = "The Warrior"
        val newName = "The Fallen Warrior"
        this.addSong(songName)
        this.editSong(songName, newName)
        this.removeSong(newName)
    }
    // Condition - there is only one song with these properties
    @Test
    fun songs_fragment_edit_remove_song_swipe_gestures() {
        val songName = "The Slayer"
        val newName = "Bulking"
        this.addSong(songName)

        // edit song
        onView(withId(R.id.songs_rv_list))
            .perform(RecyclerViewActions.scrollTo<SongAdapter.ViewHolder>(
                hasDescendant(withText(songName))))
            .perform(RecyclerViewActions.actionOnItem<ConcertAdapter.ViewHolder>(
                    hasDescendant(withText(songName)), swipeRight()))
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.song_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.song_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withId(R.id.songs_rv_list))
            .perform(RecyclerViewActions.scrollTo<SongAdapter.ViewHolder>(
                hasDescendant(withText(newName))))
            .perform(RecyclerViewActions.actionOnItem<SongAdapter.ViewHolder>(
                    hasDescendant(withText(newName)), swipeLeft()))
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been deleted")
    }
    // Condition - there is only one song with these properties
    @Test
    fun songs_fragment_search_view_filter_songs() {
        val songName = "Avengers Of Death"
        val searchValue = "aVengErs"
        this.addSong(songName)

        onView(withId(R.id.songs_search_view))
            .perform(typeText(searchValue), closeSoftKeyboard())

        onView(withText(songName)).check(matches(isDisplayed()))

        onView(withId(R.id.songs_search_view))
            .perform(typeText("2323"), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been filtered out")

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.songs_search_view))
            .perform(AndroidTestUtil.clearText(), typeText(searchValue), closeSoftKeyboard())

        this.removeSong(songName)
    }
    // Condition - there is only one song with these properties
    @Test
    fun songs_fragment_filter_button_songs() {
        val songName = "The Catalyst"
        val searchValue = "catalyst"
        this.addSong(songName)

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_filter)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.song_et_name))
            .perform(typeText(searchValue + 23), closeSoftKeyboard())
        onView(withId(R.id.song_et_release_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.song_button)).perform(click())

        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been filtered out")

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_filter)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.song_et_name))
            .perform(clearText(), typeText(searchValue), closeSoftKeyboard())
        onView(withId(R.id.song_button)).perform(click())

        this.removeSong(songName)
    }
    // Condition - there is only one album with these properties
    @Test
    fun songs_fragment_add_remove_album() {
        val albumName = "Debut Album"
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)
        this.removeAlbum(albumName)
        AndroidTestUtil.checkIfItIsNotDisplayed(albumName,
            "This album should have been deleted")
    }
    // Condition - there is only one album with these properties
    @Test
    fun songs_fragment_edit_album() {
        val albumName = "The Vikings"
        val newName = "The Anarchist Vikings"
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)
        this.editAlbum(albumName, newName)
        this.removeAlbum(newName)
    }
    // Condition - there is only one album with these properties
    @Test
    fun songs_fragment_search_view_filter_albums() {
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        val albumName = "Working and Drinking"
        val searchValue = "work"
        this.addAlbum(albumName)

        onView(withId(R.id.songs_search_view))
            .perform(typeText(searchValue), closeSoftKeyboard())

        onView(withText(albumName)).check(matches(isDisplayed()))

        onView(withId(R.id.songs_search_view))
            .perform(typeText("2323"), closeSoftKeyboard())

        AndroidTestUtil.checkIfItIsNotDisplayed(albumName,
            "This album should have been filtered out")

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.songs_search_view))
            .perform(AndroidTestUtil.clearText(), typeText(searchValue), closeSoftKeyboard())

        this.removeAlbum(albumName)
    }
    // Condition - there is only one album with these properties
    @Test
    fun songs_fragment_filter_button_album() {
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        val albumName = "Drinking and Walking"
        val searchValue = "drink"
        this.addAlbum(albumName)

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_filter)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.album_et_name))
            .perform(typeText(searchValue + 23), closeSoftKeyboard())
        onView(withId(R.id.album_et_release_date)).perform(click())
        onView(withText("OK")).perform(click())
        onView(withId(R.id.album_button)).perform(click())

        AndroidTestUtil.checkIfItIsNotDisplayed(albumName,
            "This album should have been filtered out")

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_filter)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.album_et_name))
            .perform(clearText(), typeText(searchValue), closeSoftKeyboard())
        onView(withId(R.id.album_button)).perform(click())

        this.removeAlbum(albumName)
    }
    // Condition - there is no other album or song with these names
    @Test
    fun songs_fragment_add_song_to_album() {
        val songName = "My Nemesis"
        val songName2 = "My Angel"
        val albumName = "All The Things I Hate"
        this.addSong(songName)
        this.addSong(songName2)
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)
        // add first song to album
        onView(withText(albumName)).perform(click())
        onView(withId(R.id.album_detail_bt_add_songs)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        onView(withText(songName)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(songName)).check(matches(isDisplayed()))

        // check if the added song is still available
        onView(withId(R.id.album_detail_bt_add_songs)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(albumName,
            "This song should have been added to the album")

        // add second song to album
        onView(withText(songName2)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // check if they are all displayed
        onView(withText(songName)).check(matches(isDisplayed()))
        onView(withText(songName2)).check(matches(isDisplayed()))
        onView(isRoot()).perform(pressBack())

        // check if the displayable album name is labeled on the songs
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        onView(withIndex(withText(albumName), 0)).check(matches(isDisplayed()))
        onView(withIndex(withText(albumName), 1)).check(matches(isDisplayed()))

        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        // edit album
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        val newAlbumName = "My Dear Nemesis"
        this.editAlbum(albumName, newAlbumName)

        // check if the displayable new album name is labeled on the songs
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        onView(withIndex(withText(newAlbumName), 0)).check(matches(isDisplayed()))
        onView(withIndex(withText(newAlbumName), 1)).check(matches(isDisplayed()))

        // edit one song
        val newSongName = "My Fake Angel"
        this.editSong(songName, newSongName)

        // delete the other
        this.removeSong(songName2)

        // check if the displayed name on the album changed
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        // index 1 because even though the song is not visible it is still being selected
        // Espresso :thumbs_down: :(
        onView(withIndex(withText(newAlbumName), 1)).perform(click())
        onView(withText(newSongName)).check(matches(isDisplayed()))
        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been edited")
        AndroidTestUtil.checkIfItIsNotDisplayed(songName2,
            "This song should have been removed")

        // delete album
        onView(isRoot()).perform(pressBack())
        this.removeAlbum(newAlbumName)

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(newAlbumName,
            "This album should have been deleted")
        this.removeSong(newSongName)
    }
    // Condition - there is no other album or song with these names
    @Test
    fun songs_fragment_remove_song_from_album() {
        val songName = "Raimond The Wind Walker"
        val albumName = "Raimond's Album"
        this.addSong(songName)
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)
        // add song to album
        onView(withText(albumName)).perform(click())
        onView(withId(R.id.album_detail_bt_add_songs)).perform(click())
        onView(withText(songName)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // delete song from album
        onView(withText(songName)).perform(swipeLeft())
        onView(withText(R.string.alert_dialog_positive)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))
        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been removed from the album")

        // check if the song is still on the songs list
        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        onView(withText(songName)).check(matches(isDisplayed()))

        this.removeSong(songName)
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.removeAlbum(albumName)
    }
    // Condition - there is no other album or song with these names
    @Test
    fun songs_fragment_edit_song_from_album() {
        val songName = "Raimond The Wind Walker"
        val newSongName = "The Fire Tail"
        val albumName = "Raimond's Album"
        this.addSong(songName)
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)
        // add song to album
        onView(withText(albumName)).perform(click())
        onView(withId(R.id.album_detail_bt_add_songs)).perform(click())
        onView(withText(songName)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        // edit song from album
        onView(withText(songName)).perform(swipeRight())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.song_et_name))
            .perform(clearText(), typeText(newSongName), closeSoftKeyboard())

        onView(withId(R.id.song_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been edited from the album")
        onView(withText(newSongName)).check(matches(isDisplayed()))

        onView(isRoot()).perform(pressBack())

        // check if the song changed in the song list too
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        AndroidTestUtil.checkIfItIsNotDisplayed(songName,
            "This song should have been edited from the song list")
        onView(withText(newSongName)).check(matches(isDisplayed()))

        this.removeSong(newSongName)
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.removeAlbum(albumName)
    }
    @Test
    fun songs_fragment_add_new_song_to_album() {
        val songName = "Ping Beer"
        val albumName = "Beers and pongs"
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.addAlbum(albumName)

        onView(withText(albumName)).perform(click())
        onView(withId(R.id.album_detail_bt_add_new_song)).perform(click())

        onView(withId(R.id.song_et_name)).perform(typeText(songName))

        onView(withId(R.id.song_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(songName)).check(matches(isDisplayed()))

        onView(isRoot()).perform(pressBack())
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())

        onView(withText(songName)).check(matches(isDisplayed()))

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.removeAlbum(albumName)

        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_album_mode)).perform(click())
        this.removeSong(songName)
    }
    private fun addSong(name: String) {
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.song_et_name)).perform(typeText(name))

        onView(withId(R.id.song_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(name)).check(matches(isDisplayed()))
    }
    private fun removeSong(name: String) {
        onView(withId(R.id.songs_rv_list))
            .perform(RecyclerViewActions.scrollTo<SongAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(
                RecyclerViewActions.actionOnItem<SongAdapter.ViewHolder>(
                    hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
    private fun removeAlbum(name: String) {
        onView(withId(R.id.songs_rv_albums))
            .perform(RecyclerViewActions.scrollTo<AlbumAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(
                RecyclerViewActions.actionOnItem<AlbumAdapter.ViewHolder>(
                    hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_delete)).perform(click())
        onView(withText(R.string.alert_dialog_positive)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
    private fun editSong(name: String, newName: String) {
        onView(withId(R.id.songs_rv_list))
            .perform(RecyclerViewActions.scrollTo<SongAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(RecyclerViewActions.actionOnItem<SongAdapter.ViewHolder>(
                    hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_edit)).perform(click())

        onView(withId(R.id.song_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.song_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
    private fun editAlbum(name: String, newName: String) {
        onView(withId(R.id.songs_rv_albums))
            .perform(RecyclerViewActions.scrollTo<AlbumAdapter.ViewHolder>(
                hasDescendant(withText(name))))
            .perform(RecyclerViewActions.actionOnItem<AlbumAdapter.ViewHolder>(
                    hasDescendant(withText(name)), longClick()))
        onView(withText(R.string.bt_edit)).perform(click())

        onView(withId(R.id.album_et_name))
            .perform(clearText(), typeText(newName), closeSoftKeyboard())
        onView(withId(R.id.album_button)).perform(click())

        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))
    }
    private fun addAlbum(name: String) {
        onView(withId(R.id.songs_bt_options)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.fabAnimationDelay))
        onView(withId(R.id.songs_bt_add)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.smallDelay))

        onView(withId(R.id.album_et_name)).perform(typeText(name))

        onView(withId(R.id.album_button)).perform(click())
        onView(isRoot()).perform(waitFor(ConstantsTest.maximumDelayOperations))

        onView(withText(name)).check(matches(isDisplayed()))
    }
}