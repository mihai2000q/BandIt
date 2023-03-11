package com.bandit.util

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.bandit.R

object TestUtil {
    fun login(email: String, password: String) {
        Espresso.onView(ViewMatchers.withId(R.id.login_et_email_layout))
            .perform(ViewActions.clearText(), ViewActions.typeText(email))
        Espresso.onView(ViewMatchers.withId(R.id.login_et_password_layout))
            .perform(ViewActions.typeText(password), ViewActions.pressImeActionButton())
        Espresso.onView(ViewMatchers.isRoot()).perform(AndroidTestUtil.waitFor(ConstantsTest.maximumDelayLoadingScreen))
    }
}