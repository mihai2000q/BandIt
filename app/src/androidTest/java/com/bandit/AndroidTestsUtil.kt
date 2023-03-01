package com.bandit

import androidx.test.platform.app.InstrumentationRegistry

object AndroidTestsUtil {
    fun getResourceString(id: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(id)
    }
    const val accountEmail = "Admin@Bandit.com"
    const val accountPassword = "adminbandit"
}