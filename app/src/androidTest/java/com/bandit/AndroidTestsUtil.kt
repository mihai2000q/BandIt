package com.bandit

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matcher


object AndroidTestsUtil {
    const val accountEmail = "Admin@Bandit.com"
    const val accountPassword = "adminbandit"
    const val maximumDelayLoadingScreen = 5_000L //ms
    fun getResourceString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id)
    }
    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds";
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay);
            }
        }
    }
    fun login() {
        Espresso.onView(ViewMatchers.withId(R.id.login_et_email)).perform(ViewActions.clearText())
        Espresso.onView(ViewMatchers.withId(R.id.login_et_email))
            .perform(ViewActions.typeText(accountEmail))
        Espresso.onView(ViewMatchers.withId(R.id.login_et_password))
            .perform(ViewActions.typeText(accountPassword))
        Espresso.onView(ViewMatchers.withId(R.id.login_bt_login)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(maximumDelayLoadingScreen))
    }
}