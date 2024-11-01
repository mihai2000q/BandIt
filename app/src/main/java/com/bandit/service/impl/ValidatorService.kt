package com.bandit.service.impl

import android.app.Activity
import android.util.Patterns
import android.widget.EditText
import com.bandit.R
import com.bandit.auth.Authenticator
import com.bandit.constant.Constants
import com.bandit.service.IValidatorService
import com.google.android.material.textfield.TextInputLayout

class ValidatorService(
    private val activity: Activity
) : IValidatorService {
    override fun validateEmail(editText: EditText, editTextLayout: TextInputLayout) : Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getText(R.string.et_email_validation_empty)
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editText.text).matches()) {
            editTextLayout.error = activity.resources.getText(R.string.et_email_validation_email)
            return false
        }
        editTextLayout.error = null
        return true
    }
    override fun validatePassword(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_empty)
            return false
        }
        if(editText.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_minimum)
            return false
        }
        editTextLayout.error = null
        return true
    }
    override suspend fun validateEmailVerified(
        editText: EditText,
        editTextLayout: TextInputLayout,
        login: suspend () -> Boolean?,
        authenticator: Authenticator
    ): Boolean? {
        return if (authenticator.currentUser!!.isEmailVerified) {
            editTextLayout.error = null
            login()
        }
        else {
            editTextLayout.error = activity.resources.getString(R.string.et_email_validation_email_verified)
            authenticator.signOut()
            null
        }
    }

    override fun validateName(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_name_validation)
            return false
        }
        editTextLayout.error = null
        return true
    }

    override fun validateNickname(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_nickname_validation)
            return false
        }
        editTextLayout.error = null
        return true
    }

    override fun validateDate(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_date_validation)
            return false
        }
        editTextLayout.error = null
        return true
    }

    override fun validateTime(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_time_validation)
            return false
        }
        editTextLayout.error = null
        return true
    }
    
    override fun validateStrictDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation)
            return false
        }
        if(editText.text.length != 5 || Integer.parseInt(editText.text[3].toString()) >= 6) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
            return false
        }
        editTextLayout.error = null
        return true
    }

    override fun validateDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(!editText.text.isNullOrBlank() &&
            (editText.text.length != 5 ||
            Integer.parseInt(editText.text[3].toString()) >= 6)
        ) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
            return false
        }
        editTextLayout.error = null
        return true
    }
}