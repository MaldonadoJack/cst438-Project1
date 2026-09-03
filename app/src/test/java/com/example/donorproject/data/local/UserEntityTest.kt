package com.example.donorproject.data.local

import org.junit.Assert.assertEquals
import org.junit.Test

class UserEntityTest {

    @Test
    fun userEntityRetainsTheValuesItWasConstructedWith() {
        val user = UserEntity(id = 7, username = "julian", passwordHash = "stored-hash")

        assertEquals(7, user.id)
        assertEquals("julian", user.username)
        assertEquals("stored-hash", user.passwordHash)
    }

    @Test
    fun userEntityDefaultsIdToZeroSoRoomCanAutoGenerateIt() {
        val user = UserEntity(username = "julian", passwordHash = "stored-hash")

        assertEquals(0, user.id)
    }
}
