package com.bandit.service.impl

import android.app.Activity
import android.util.Patterns
import android.widget.EditText
import com.bandit.R
import com.bandit.auth.Authenticator
import com.bandit.constant.Constants
import com.bandit.di.DILocator
import com.bandit.service.IValidatorService

class ValidatorService(
    private val activity: Activity
) : IValidatorService {
    override fun validateEmail(editText: EditText) : Boolean {
        if(editText.text.isNullOrEmpty()) {
            editText.error = activity.resources.getText(R.string.et_email_validation_empty)
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editText.text).matches()) {
            editText.error = activity.resources.getText(R.string.et_email_validation_email)
            return false
        }
        return true
    }
    override fun validatePassword(editText: EditText): Boolean {
        if(editText.text.isNullOrEmpty()) {
            editText.error = activity.resources.getText(R.string.et_pass_validation_empty)
            return false
        }
        if(editText.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
            editText.error = activity.resources.getText(R.string.et_pass_validation_minimum)
            return false
        }
        return true
    }
    override suspend fun validateEmailVerified(
        editText: EditText,
        login: suspend () -> Boolean?,
        authenticator: Authenticator
    ): Boolean? {
        return if (authenticator.currentUser!!.isEmailVerified)
            login()
        else {
            editText.error = activity.resources.getString(R.string.et_email_validation_email_verified)
            authenticator.signOut()
            null
        }
    }
}