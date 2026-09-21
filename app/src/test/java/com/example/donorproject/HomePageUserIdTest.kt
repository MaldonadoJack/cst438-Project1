package com.example.donorproject

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomePageUserIdTest {

    @Test
    fun aMissingIntentIsRejected() {
        assertEquals(-1, HomePage.userIdFromIntent(null))
        assertFalse(HomePage.isValidUserId(HomePage.userIdFromIntent(null)))
    }

    @Test
    fun aNonPositiveUserIdIsRejected() {
        assertFalse(HomePage.isValidUserId(-1))
        assertFalse(HomePage.isValidUserId(0))
    }

    @Test
    fun aGeneratedUserIdIsAccepted() {
        assertTrue(HomePage.isValidUserId(1))
        assertTrue(HomePage.isValidUserId(42))
    }
}
