package com.example.donorproject

import com.example.donorproject.validation.CredentialValidator
import com.example.donorproject.validation.ValidationResult

data class LoginFormValidationResult(
    val usernameError: String? = null,
    val passwordError: String? = null
) {
    val isValid: Boolean
    get() = usernameError == null && passwordError == null
}

object LoginFormValidator {

    fun validate(
        username: String,
        password: String
    ): LoginFormValidationResult {
        val usernameResult =
            CredentialValidator.validateUsernameFormat(username)

        val passwordResult =
            CredentialValidator.validatePassword(password)

        return LoginFormValidationResult(
            usernameError = usernameResult.errorMessage(),
            passwordError = passwordResult.errorMessage()
        )
    }

    private fun ValidationResult.errorMessage(): String? =
        when (this) {
            ValidationResult.Valid -> null
            is ValidationResult.Invalid -> error.message
        }
}