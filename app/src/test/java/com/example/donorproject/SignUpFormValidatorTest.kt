package com.example.donorproject

import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Runs on the JVM against [FakeUserDao], so these cases cover the validator's own rules
 * rather than Room's behavior. [UsernameAvailabilityTest] covers the real database.
 */
class SignUpFormValidatorTest {

    /** [insert] throws because validation must never write. */
    private class FakeUserDao(private val takenUsernames: Set<String> = emptySet()) : UserDao {

        override suspend fun insert(user: UserEntity): Long =
            throw UnsupportedOperationException("Validation must not insert rows")

        override suspend fun findByUsername(username: String): UserEntity? =
            if (username in takenUsernames) {
                UserEntity(id = 1, username = username, passwordHash = FAKE_HASH)
            } else {
                null
            }
    }

    @Test
    fun availableUsernameWithMatchingPasswordsIsAccepted() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "password",
            confirmPassword = "password",
            userDao = FakeUserDao()
        )

        assertTrue(result.isValid)
        assertNull(result.usernameError)
        assertNull(result.passwordError)
        assertNull(result.confirmPasswordError)
    }

    @Test
    fun blankUsernameIsRejected() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "   ",
            password = "password",
            confirmPassword = "password",
            userDao = FakeUserDao()
        )

        assertFalse(result.isValid)
        assertEquals(ValidationError.USERNAME_BLANK.message, result.usernameError)
    }

    @Test
    fun takenUsernameIsRejected() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "password",
            confirmPassword = "password",
            userDao = FakeUserDao(takenUsernames = setOf("julian"))
        )

        assertFalse(result.isValid)
        assertEquals(ValidationError.USERNAME_TAKEN.message, result.usernameError)
    }

    /** Matching is case-sensitive, so this is a different account. */
    @Test
    fun usernameDifferingOnlyByCaseIsAccepted() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "Julian",
            password = "password",
            confirmPassword = "password",
            userDao = FakeUserDao(takenUsernames = setOf("julian"))
        )

        assertTrue(result.isValid)
    }

    @Test
    fun blankPasswordIsRejected() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "",
            confirmPassword = "",
            userDao = FakeUserDao()
        )

        assertFalse(result.isValid)
        assertEquals(ValidationError.PASSWORD_BLANK.message, result.passwordError)
    }

    /** There are no length or character-class rules, only a blank check. */
    @Test
    fun shortLowercasePasswordIsAccepted() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "abc",
            confirmPassword = "abc",
            userDao = FakeUserDao()
        )

        assertTrue(result.isValid)
    }

    @Test
    fun mismatchedPasswordsAreRejected() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "password",
            confirmPassword = "different",
            userDao = FakeUserDao()
        )

        assertFalse(result.isValid)
        assertNull(result.passwordError)
        assertEquals(ValidationError.PASSWORD_MISMATCH.message, result.confirmPasswordError)
    }

    /** A blank password already reports its own error, so it is not also a mismatch. */
    @Test
    fun mismatchIsNotReportedWhenThePasswordIsBlank() = runBlocking {
        val result = SignUpFormValidator.validate(
            username = "julian",
            password = "",
            confirmPassword = "password",
            userDao = FakeUserDao()
        )

        assertFalse(result.isValid)
        assertEquals(ValidationError.PASSWORD_BLANK.message, result.passwordError)
        assertNull(result.confirmPasswordError)
    }

    private companion object {
        const val FAKE_HASH = "fake-hash-aaa111"
    }
}
