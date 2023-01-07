package com.bandit.helper

import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.random.Random

object AndroidUtils {
    fun generateRandomId() = Random.nextInt(Constants.INT_MAX)
    //Context.INPUT_METHOD_SERVICE
    fun hideKeyboard(activity: Activity, inputMethodService: String, view: View) {
        val input = activity.getSystemService(inputMethodService) as InputMethodManager
        input.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
