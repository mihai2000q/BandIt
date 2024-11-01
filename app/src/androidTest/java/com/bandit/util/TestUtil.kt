package com.bandit.util

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.bandit.R

object TestUtil {
    fun login(email: String, password: String) {
        Espresso.onView(ViewMatchers.withId(R.id.login_et_email))
            .perform(ViewActions.clearText(), ViewActions.typeText(email))
        Espresso.onView(ViewMatchers.withId(R.id.login_et_password))
            .perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
        Espresso.onView(ViewMatchers.withId(R.id.login_bt_login)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.isRoot()).perform(AndroidTestUtil.waitFor(ConstantsTest.maximumDelayLoadingScreen))
    }
}