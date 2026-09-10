package com.example.donorproject.validation

import org.junit.Assert.assertEquals
import org.junit.Test

class CredentialValidatorTest {

    @Test
    fun blankUsernameIsRejected() {
        assertEquals(
            ValidationResult.Invalid(ValidationError.USERNAME_BLANK),
            CredentialValidator.validateUsernameFormat("")
        )
    }

    @Test
    fun whitespaceOnlyUsernameIsRejected() {
        assertEquals(
            ValidationResult.Invalid(ValidationError.USERNAME_BLANK),
            CredentialValidator.validateUsernameFormat("   ")
        )
        assertEquals(
            ValidationResult.Invalid(ValidationError.USERNAME_BLANK),
            CredentialValidator.validateUsernameFormat("\t\n")
        )
    }

    @Test
    fun nonBlankUsernameFormatIsAccepted() {
        assertEquals(
            ValidationResult.Valid,
            CredentialValidator.validateUsernameFormat("julian")
        )
    }

    @Test
    fun blankPasswordIsRejected() {
        assertEquals(
            ValidationResult.Invalid(ValidationError.PASSWORD_BLANK),
            CredentialValidator.validatePassword("")
        )
    }

    @Test
    fun whitespaceOnlyPasswordIsRejected() {
        assertEquals(
            ValidationResult.Invalid(ValidationError.PASSWORD_BLANK),
            CredentialValidator.validatePassword("    ")
        )
    }

    @Test
    fun nonBlankPasswordIsAccepted() {
        assertEquals(ValidationResult.Valid, CredentialValidator.validatePassword("a passphrase"))
    }

    @Test
    fun singleCharacterPasswordIsAccepted() {
        assertEquals(ValidationResult.Valid, CredentialValidator.validatePassword("a"))
    }

    @Test
    fun passwordWithoutUppercaseDigitsOrSymbolsIsAccepted() {
        assertEquals(ValidationResult.Valid, CredentialValidator.validatePassword("password"))
    }
}
