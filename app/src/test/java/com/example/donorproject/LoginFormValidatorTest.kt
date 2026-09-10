package com.example.donorproject

import com.example.donorproject.validation.ValidationError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginFormValidatorTest {

    @Test
    fun validUsernameAndPasswordAreAccepted() {
        val result = LoginFormValidator.validate(
            username = "daniel",
            password = "password"
        )

        assertTrue(result.isValid)
        assertNull(result.usernameError)
        assertNull(result.passwordError)
    }

    @Test
    fun blankUsernameIsRejected() {
        val result = LoginFormValidator.validate(
            username = "   ",
            password = "password"
        )

        assertFalse(result.isValid)
        assertEquals(
            ValidationError.USERNAME_BLANK.message,
            result.usernameError
        )
        assertNull(result.passwordError)
    }

    @Test
    fun blankPasswordIsRejected() {
        val result = LoginFormValidator.validate(
            username = "daniel",
            password = ""
        )

        assertFalse(result.isValid)
        assertNull(result.usernameError)
        assertEquals(
            ValidationError.PASSWORD_BLANK.message,
            result.passwordError
        )
    }

    @Test
    fun bothBlankFieldsAreRejected() {
        val result = LoginFormValidator.validate(
            username = "",
            password = ""
        )

        assertFalse(result.isValid)
        assertEquals(
            ValidationError.USERNAME_BLANK.message,
            result.usernameError
        )
        assertEquals(
            ValidationError.PASSWORD_BLANK.message,
            result.passwordError
        )
    }
}