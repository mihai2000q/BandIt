package com.bandit.service

import android.widget.EditText
import com.bandit.auth.Authenticator
import com.google.android.material.textfield.TextInputLayout

interface IValidatorService {
    fun validateEmail(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validatePassword(editText: EditText, editTextLayout: TextInputLayout): Boolean
    suspend fun validateEmailVerified(
        editText: EditText,
        editTextLayout: TextInputLayout,
        login: suspend () -> Boolean?,
        authenticator: Authenticator
    ): Boolean?

    fun validateName(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validateNickname(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validateDate(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validateTime(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validateStrictDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean
    fun validateDuration(editText: EditText, editTextLayout: TextInputLayout): Boolean
}