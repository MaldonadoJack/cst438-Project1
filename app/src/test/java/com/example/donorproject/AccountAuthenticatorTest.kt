package com.example.donorproject

import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.PasswordHasher
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Runs on the JVM against [FakeUserDao], so these cases cover [AccountAuthenticator]
 * rather than Room. Stored passwords use the real [PasswordHasher].
 */
class AccountAuthenticatorTest {

    private class FakeUserDao : UserDao {
        private val users = mutableMapOf<String, UserEntity>()

        override suspend fun insert(user: UserEntity): Long {
            val id = users.size + 1
            users[user.username] = user.copy(id = id)
            return id.toLong()
        }

        override suspend fun findByUsername(username: String): UserEntity? = users[username]
    }

    @Test
    fun storedUserWithTheCorrectPasswordReturnsSuccess() = runBlocking {
        val userDao = FakeUserDao()
        userDao.insert(
            UserEntity(username = USERNAME, passwordHash = PasswordHasher.hash(PASSWORD))
        )

        val outcome = AccountAuthenticator.logIn(
            username = "  $USERNAME  ",
            password = PASSWORD,
            userDao = userDao
        )

        assertEquals(LoginOutcome.Success(USERNAME), outcome)
    }

    @Test
    fun storedUserWithAnIncorrectPasswordIsRejected() = runBlocking {
        val userDao = FakeUserDao()
        userDao.insert(
            UserEntity(username = USERNAME, passwordHash = PasswordHasher.hash(PASSWORD))
        )

        val outcome = AccountAuthenticator.logIn(
            username = USERNAME,
            password = "wrong-password",
            userDao = userDao
        )

        assertRejectedWithInvalidCredentials(outcome)
    }

    @Test
    fun unknownUsernameIsRejectedWithTheGenericCredentialsMessage() = runBlocking {
        val outcome = AccountAuthenticator.logIn(
            username = USERNAME,
            password = PASSWORD,
            userDao = FakeUserDao()
        )

        assertRejectedWithInvalidCredentials(outcome)
    }

    private fun assertRejectedWithInvalidCredentials(outcome: LoginOutcome) {
        assertTrue("Expected a rejection but got $outcome", outcome is LoginOutcome.Rejected)

        val errors = (outcome as LoginOutcome.Rejected).errors
        assertEquals(ValidationError.INVALID_CREDENTIALS.message, errors.passwordError)
    }

    private companion object {
        const val USERNAME = "julian"
        const val PASSWORD = "password"
    }
}
