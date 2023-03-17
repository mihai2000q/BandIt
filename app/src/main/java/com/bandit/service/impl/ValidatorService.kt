package com.bandit.service.impl

import android.app.Activity
import android.util.Patterns
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
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
            this.applyEmptyListener(editText, editTextLayout, R.string.et_email_validation_empty)
            return false
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(editText.text).matches()) {
            editTextLayout.error = activity.resources.getText(R.string.et_email_validation_email)
            editText.doOnTextChanged { text, _, _, _ ->
                if(Patterns.EMAIL_ADDRESS.matcher(text!!).matches()) {
                    editTextLayout.error = null
                } else {
                    editTextLayout.error = activity.resources.getText(R.string.et_email_validation_email)
                }
            }
            return false
        }
        return true
    }
    override fun validatePassword(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        val passValidator = {
            editText.doOnTextChanged { text, _, _, _ ->
                if (text!!.isBlank()) {
                    editTextLayout.error =
                        activity.resources.getText(R.string.et_pass_validation_empty)
                } else if (text.length < Constants.PASSWORD_MIN_CHARACTERS) {
                    editTextLayout.error =
                        activity.resources.getText(R.string.et_pass_validation_minimum)
                } else
                    editTextLayout.error = null
            }
        }
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_empty)
            passValidator.invoke()
            return false
        }
        if(editText.text.length < Constants.PASSWORD_MIN_CHARACTERS) {
            editTextLayout.error = activity.resources.getText(R.string.et_pass_validation_minimum)
            passValidator.invoke()
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
            this.applyEmptyListener(editText, editTextLayout, R.string.et_name_validation)
            return false
        }
        return true
    }

    override fun validateNickname(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_nickname_validation)
            this.applyEmptyListener(editText, editTextLayout, R.string.et_nickname_validation)
            return false
        }
        return true
    }

    override fun validateDate(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_date_validation)
            this.applyEmptyListener(editText, editTextLayout, R.string.et_date_validation)
            return false
        }
        return true
    }

    override fun validateTime(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_time_validation)
            this.applyEmptyListener(editText, editTextLayout, R.string.et_time_validation)
            return false
        }
        return true
    }
    
    override fun validateStrictDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if(editText.text.isNullOrBlank()) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation)
            this.applyEmptyListener(editText, editTextLayout, R.string.et_duration_validation)
            return false
        }
        if(editText.text.length != 5 || Integer.parseInt(editText.text[3].toString()) >= 6) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
            editText.doOnTextChanged { text, _, _, _ ->
                if(text!!.length == 5 && Integer.parseInt(text[3].toString()) < 6) {
                    editTextLayout.error = null
                } else {
                    editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
                }
            }
            return false
        }
        return true
    }

    override fun validateDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean {
        if((!editText.text.isNullOrBlank() &&
            editText.text.length != 5) ||
            Integer.parseInt(editText.text[3].toString()) >= 6
        ) {
            editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
            editText.doOnTextChanged { text, _, _, _ ->
                if(editText.text.isNullOrBlank() && text!!.length == 5 && Integer.parseInt(text[3].toString()) < 6) {
                    editTextLayout.error = null
                } else {
                    editTextLayout.error = activity.resources.getString(R.string.et_duration_validation_valid)
                }
            }
            return false
        }
        return true
    }

    private fun applyEmptyListener(editText: EditText, editTextLayout: TextInputLayout, stringId: Int) {
        editText.doOnTextChanged { text, _, _, _ ->
            if(text!!.isNotEmpty()) {
                editTextLayout.error = null
            } else {
                editTextLayout.error = activity.resources.getString(stringId)
            }

        }
    }
}