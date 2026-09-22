package com.example.donorproject

import android.database.sqlite.SQLiteConstraintException
import com.example.donorproject.data.local.AccountDao
import com.example.donorproject.validation.CredentialValidator
import com.example.donorproject.validation.PasswordHasher
import com.example.donorproject.validation.ValidationError
import com.example.donorproject.validation.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class AccountUpdateValidationResult(
    val usernameError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isValid: Boolean
        get() = usernameError == null &&
                passwordError == null &&
                confirmPasswordError == null
}

sealed interface AccountUpdateOutcome {
    data class Updated(val username: String) : AccountUpdateOutcome

    data class Rejected(
        val errors: AccountUpdateValidationResult
    ) : AccountUpdateOutcome

    data object UserNotFound : AccountUpdateOutcome
}

object AccountUpdater {

    suspend fun updateAccount(
        userId: Int,
        username: String,
        newPassword: String,
        confirmPassword: String,
        accountDao: AccountDao
    ): AccountUpdateOutcome {
        val currentUser = accountDao.findById(userId)
            ?: return AccountUpdateOutcome.UserNotFound

        val updatedUsername = username.trim()

        val usernameResult =
            CredentialValidator.validateUsernameFormat(updatedUsername)

        var usernameError = usernameResult.errorMessage()

        if (usernameError == null) {
            val matchingUser = accountDao.findByUsername(updatedUsername)

            if (matchingUser != null && matchingUser.id != userId) {
                usernameError = ValidationError.USERNAME_TAKEN.message
            }
        }

        val isChangingPassword =
            newPassword.isNotEmpty() || confirmPassword.isNotEmpty()

        val passwordResult =
            if (isChangingPassword) {
                CredentialValidator.validatePassword(newPassword)
            } else {
                ValidationResult.Valid
            }

        val passwordError = passwordResult.errorMessage()

        val confirmPasswordError =
            if (
                isChangingPassword &&
                passwordResult is ValidationResult.Valid &&
                newPassword != confirmPassword
            ) {
                ValidationError.PASSWORD_MISMATCH.message
            } else {
                null
            }

        val validation = AccountUpdateValidationResult(
            usernameError = usernameError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError
        )

        if (!validation.isValid) {
            return AccountUpdateOutcome.Rejected(validation)
        }

        val updatedPasswordHash =
            if (isChangingPassword) {
                withContext(Dispatchers.Default) {
                    PasswordHasher.hash(newPassword)
                }
            } else {
                currentUser.passwordHash
            }

        return try {
            val updatedRows = accountDao.updateAccount(
                userId = userId,
                username = updatedUsername,
                passwordHash = updatedPasswordHash
            )

            if (updatedRows == 0) {
                AccountUpdateOutcome.UserNotFound
            } else {
                AccountUpdateOutcome.Updated(updatedUsername)
            }
        } catch (exception: SQLiteConstraintException) {
            AccountUpdateOutcome.Rejected(
                AccountUpdateValidationResult(
                    usernameError = ValidationError.USERNAME_TAKEN.message
                )
            )
        }
    }

    private fun ValidationResult.errorMessage(): String? =
        when (this) {
            ValidationResult.Valid -> null
            is ValidationResult.Invalid -> error.message
        }
}