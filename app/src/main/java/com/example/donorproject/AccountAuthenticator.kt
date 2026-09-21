package com.example.donorproject

import com.example.donorproject.data.local.UserDao
import com.example.donorproject.validation.PasswordHasher
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface LoginOutcome {

    /** [userId] is the stored [UserEntity.id]; [username] is the trimmed match. */
    data class Success(val userId: Int, val username: String) : LoginOutcome

    /** Carries the per-field messages the form should display. Nobody was signed in. */
    data class Rejected(val errors: LoginFormValidationResult) : LoginOutcome
}

object AccountAuthenticator {

    suspend fun logIn(
        username: String,
        password: String,
        userDao: UserDao
    ): LoginOutcome {
        val formResult = LoginFormValidator.validate(username = username, password = password)
        if (!formResult.isValid) {
            return LoginOutcome.Rejected(formResult)
        }

        // Sign-up stores the trimmed username, so the lookup has to use the same value.
        val enteredUsername = username.trim()
        val user = userDao.findByUsername(enteredUsername)
            ?: return LoginOutcome.Rejected(
                LoginFormValidationResult(passwordError = ValidationError.INVALID_CREDENTIALS.message)
            )

        // Key derivation is deliberately slow, so keep it off the main thread.
        val passwordMatches = withContext(Dispatchers.Default) {
            PasswordHasher.verify(password, user.passwordHash)
        }

        return if (passwordMatches) {
            LoginOutcome.Success(userId = user.id, username = enteredUsername)
        } else {
            LoginOutcome.Rejected(
                LoginFormValidationResult(passwordError = ValidationError.INVALID_CREDENTIALS.message)
            )
        }
    }
}