package com.bandit.service

import android.widget.EditText
import com.bandit.auth.Authenticator

interface IValidatorService {
    fun validateEmail(editText: EditText): Boolean
    fun validatePassword(editText: EditText): Boolean
    suspend fun validateEmailVerified(
        editText: EditText,
        login: suspend () -> Boolean?,
        authenticator: Authenticator
    ): Boolean?
    fun validateName(editText: EditText): Boolean
    fun validateNickname(editText: EditText): Boolean
    fun validateDate(editText: EditText): Boolean
    fun validateTime(editText: EditText): Boolean
    fun validateDuration(editText: EditText): Boolean
}