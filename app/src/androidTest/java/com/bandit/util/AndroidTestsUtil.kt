package com.bandit.util

import android.app.Instrumentation
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.platform.app.InstrumentationRegistry
import com.bandit.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher


object AndroidTestsUtil {
    fun getResourceString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id)
    }
    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "wait for " + delay + "milliseconds"
            }

            override fun getConstraints(): Matcher<View> {
                return ViewMatchers.isRoot()
            }

            override fun perform(uiController: UiController, view: View) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }
    fun withIndex(matcher: Matcher<View?>, index: Int): Matcher<View?> {
        return object : TypeSafeMatcher<View>() {
            var currentIndex = 0
            override fun describeTo(description: Description) {
                description.appendText("with index: ")
                description.appendValue(index)
                matcher.describeTo(description)
            }

            override fun matchesSafely(view: View?): Boolean {
                return matcher.matches(view) && currentIndex++ == index
            }
        }
    }
    fun pressEnter(childId: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Perform enter action on this view"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(
                    isAssignableFrom(EditText::class.java),
                    isAssignableFrom(TextView::class.java)
                )
            }

            override fun perform(uiController: UiController?, view: View?) {
                object : Thread() {
                    override fun run() {
                        try {
                            val inst = Instrumentation()
                            inst.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER)
                        } catch (e: Exception) {
                            Log.e("Exception when sendKeyDownUpSync", e.toString())
                        }
                    }
                }.start()
            }
        }
    }
    fun clearText(childId: Int): ViewAction {
        return object : ViewAction {
            override fun getDescription(): String {
                return "Perform action on this view"
            }

            override fun getConstraints(): Matcher<View> {
                return allOf(
                    isAssignableFrom(EditText::class.java),
                    isAssignableFrom(TextView::class.java)
                )
            }

            override fun perform(uiController: UiController?, view: View?) {
                view?.findViewById<EditText>(childId)?.setText("")
            }
        }
    }
    fun login(email: String, password: String) {
        Espresso.onView(ViewMatchers.withId(R.id.login_et_email))
            .perform(ViewActions.clearText(), ViewActions.typeText(email))
        Espresso.onView(ViewMatchers.withId(R.id.login_et_password))
            .perform(ViewActions.typeText(password), ViewActions.pressImeActionButton())
        Espresso.onView(ViewMatchers.isRoot()).perform(waitFor(ConstantsTest.maximumDelayLoadingScreen))
    }
}