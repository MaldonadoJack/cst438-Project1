package com.example.donorproject

import android.database.sqlite.SQLiteConstraintException
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.PasswordHasher
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed interface SignUpOutcome {

    /** [username] is the trimmed value that was actually stored. */
    data class Created(val username: String) : SignUpOutcome

    /** Carries the per-field messages the form should display. No row was written. */
    data class Rejected(val errors: SignUpFormValidationResult) : SignUpOutcome
}

/**
 * Creates accounts. Lives outside the composable so the save path can be exercised
 * against a real database rather than only through the UI, and so nothing here can
 * navigate: creating an account deliberately does not sign the new user in.
 */
object AccountCreator {

    /**
     * Validates, hashes, and inserts in one step. Returns [SignUpOutcome.Rejected] instead
     * of throwing, so a duplicate username surfaces as a field error rather than a crash.
     */
    suspend fun createAccount(
        username: String,
        password: String,
        confirmPassword: String,
        userDao: UserDao
    ): SignUpOutcome {
        // The same value has to reach both the uniqueness check and the insert, or
        // validation passes on one string while the unique index rejects another.
        val newUsername = username.trim()

        val validation = SignUpFormValidator.validate(
            username = newUsername,
            password = password,
            confirmPassword = confirmPassword,
            userDao = userDao
        )

        if (!validation.isValid) {
            return SignUpOutcome.Rejected(validation)
        }

        val passwordHash = withContext(Dispatchers.Default) {
            PasswordHasher.hash(password)
        }

        return try {
            userDao.insert(UserEntity(username = newUsername, passwordHash = passwordHash))
            SignUpOutcome.Created(newUsername)
        } catch (exception: SQLiteConstraintException) {
            // The unique index is the real authority: a username can be claimed between
            // the check above and this insert.
            SignUpOutcome.Rejected(
                SignUpFormValidationResult(usernameError = ValidationError.USERNAME_TAKEN.message)
            )
        }
    }
}
