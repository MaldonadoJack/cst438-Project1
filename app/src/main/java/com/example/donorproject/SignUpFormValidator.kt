package com.example.donorproject

import com.example.donorproject.data.local.UserDao
import com.example.donorproject.validation.CredentialValidator
import com.example.donorproject.validation.ValidationError
import com.example.donorproject.validation.ValidationResult
import com.example.donorproject.validation.validateNewUsername

data class SignUpFormValidationResult(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isValid: Boolean
        get() = usernameError == null && passwordError == null && confirmPasswordError == null
}

object SignUpFormValidator {
    suspend fun validate(
        username: String,
        password: String,
        confirmPassword: String,
        userDao: UserDao
    ): SignUpFormValidationResult {
        val usernameResult = validateNewUsername(username, userDao)
        val passwordResult = CredentialValidator.validatePassword(password)

        val confirmError = if (passwordResult is ValidationResult.Valid && password != confirmPassword) {
            ValidationError.PASSWORD_MISMATCH.message
        } else {
            null
        }

        return SignUpFormValidationResult(
            usernameError = usernameResult.errorMessage(),
            passwordError = passwordResult.errorMessage(),
            confirmPasswordError = confirmError
        )
    }

    private fun ValidationResult.errorMessage(): String? =
        when (this) {
            ValidationResult.Valid -> null
            is ValidationResult.Invalid -> error.message
        }
}