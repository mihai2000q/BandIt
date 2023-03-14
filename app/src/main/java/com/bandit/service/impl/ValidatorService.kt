package com.bandit.service.impl

import android.app.Activity
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
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
            this.applyEmptyListener(editText, editTextLayout)
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editText.text).matches()) {
            editTextLayout.error = activity.resources.getText(R.string.et_email_validation_email)
            return false
        }
        return true
    }
    override fun validatePassword(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_empty)
            this.applyEmptyListener(editText, editTextLayout)
            return false
        }
        if(editText.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_minimum)
            return false
        }
        return true
    }
    override suspend fun validateEmailVerified(
        editText: EditText,
        editTextLayout: TextInputLayout,
        login: suspend () -> Boolean?,
        authenticator: Authenticator
    ): Boolean? {
        return if (authenticator.currentUser!!.isEmailVerified)
            login()
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
        return true
    }

    override fun validateNickname(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_nickname_validation)
            return false
        }
        return true
    }

    override fun validateDate(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_date_validation)
            return false
        }
        return true
    }

    override fun validateTime(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_time_validation)
            return false
        }
        return true
    }
    
    override fun validateDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation)
            return false
        }
        if(editText.text.length != 5) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
            return false
        }
        return true
    }

    private fun applyEmptyListener(editText: EditText, editTextLayout: TextInputLayout) {
        editText.addTextChangedListener {
            object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if(s.toString().isNotBlank()) {
                        editTextLayout.error = null
                        editText.removeTextChangedListener(this)
                    }
                }

            }
        }
    }
}