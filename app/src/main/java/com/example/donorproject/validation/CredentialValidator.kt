package com.example.donorproject.validation

import com.example.donorproject.data.local.UserDao

enum class ValidationError(val message: String) {
    USERNAME_BLANK("Username cannot be blank"),
    USERNAME_TAKEN("That username is already taken"),
    PASSWORD_BLANK("Password cannot be blank"),
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val error: ValidationError) : ValidationResult
}

object CredentialValidator {

    /** [String.isBlank] treats both empty and whitespace-only input as blank. */
    fun validateUsernameFormat(username: String): ValidationResult =
        if (username.isBlank()) {
            ValidationResult.Invalid(ValidationError.USERNAME_BLANK)
        } else {
            ValidationResult.Valid
        }

    /** Any nonblank password is valid; there are no length or character-class rules. */
    fun validatePassword(password: String): ValidationResult =
        if (password.isBlank()) {
            ValidationResult.Invalid(ValidationError.PASSWORD_BLANK)
        } else {
            ValidationResult.Valid
        }
}

/**
 * Reads through [UserDao.findByUsername] and never writes. [username] is used exactly as
 * supplied and matched case-sensitively, so `Julian` and `julian` are distinct usernames.
 *
 * A [ValidationResult.Valid] result does not guarantee a later insert will succeed, since
 * another caller could claim the username first. The unique index on `users.username`
 * remains the authority.
 */
suspend fun validateNewUsername(username: String, userDao: UserDao): ValidationResult {
    val formatResult = CredentialValidator.validateUsernameFormat(username)
    if (formatResult is ValidationResult.Invalid) {
        return formatResult
    }

    return if (userDao.findByUsername(username) == null) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid(ValidationError.USERNAME_TAKEN)
    }
}
