package com.example.donorproject.validation

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Uses a fresh in-memory database per test. The inserts here are test setup only; the
 * validation logic under test never writes.
 */
@RunWith(AndroidJUnit4::class)
class UsernameAvailabilityTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    @Before
    fun createInMemoryDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = database.userDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun unusedUsernameIsAccepted() = runBlocking {
        assertEquals(ValidationResult.Valid, validateNewUsername("julian", userDao))
    }

    @Test
    fun existingUsernameIsRejected() = runBlocking {
        userDao.insert(UserEntity(username = "julian", passwordHash = FAKE_HASH))

        assertEquals(
            ValidationResult.Invalid(ValidationError.USERNAME_TAKEN),
            validateNewUsername("julian", userDao)
        )
    }

    @Test
    fun blankUsernameIsRejectedByTheDatabaseBackedCheck() = runBlocking {
        assertEquals(
            ValidationResult.Invalid(ValidationError.USERNAME_BLANK),
            validateNewUsername("   ", userDao)
        )
    }

    @Test
    fun usernameDifferingOnlyByCaseRemainsAvailable() = runBlocking {
        userDao.insert(UserEntity(username = "julian", passwordHash = FAKE_HASH))

        assertEquals(ValidationResult.Valid, validateNewUsername("Julian", userDao))
    }

    private companion object {
        const val FAKE_HASH = "fake-hash-aaa111"
    }
}
