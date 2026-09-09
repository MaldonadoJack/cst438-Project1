package com.example.donorproject.data.local

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Uses a fresh in-memory database per test so no data persists between runs.
 * Password hashes here are obviously fake placeholders, not real credentials.
 */
@RunWith(AndroidJUnit4::class)
class UserDaoTest {

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
    fun insertedUserIsRetrievableByUsername() = runBlocking {
        val rowId = userDao.insert(UserEntity(username = USERNAME, passwordHash = FIRST_HASH))

        assertTrue("expected a generated row id but got $rowId", rowId > 0)

        val stored = userDao.findByUsername(USERNAME)

        assertNotNull("expected to find the user that was just inserted", stored)
        assertEquals(rowId.toInt(), stored!!.id)
        assertEquals(USERNAME, stored.username)
        assertEquals(FIRST_HASH, stored.passwordHash)
    }

    @Test
    fun duplicateUsernameIsRejectedAndLeavesTheOriginalRowIntact() = runBlocking {
        val originalRowId = userDao.insert(UserEntity(username = USERNAME, passwordHash = FIRST_HASH))

        assertThrows(SQLiteConstraintException::class.java) {
            runBlocking {
                userDao.insert(UserEntity(username = USERNAME, passwordHash = SECOND_HASH))
            }
        }

        val stored = userDao.findByUsername(USERNAME)

        assertNotNull("the original user should survive a rejected duplicate insert", stored)
        assertEquals(originalRowId.toInt(), stored!!.id)
        assertEquals(FIRST_HASH, stored.passwordHash)
    }

    @Test
    fun unknownUsernameReturnsNull() = runBlocking {
        userDao.insert(UserEntity(username = USERNAME, passwordHash = FIRST_HASH))

        assertNull(userDao.findByUsername("someone-who-was-never-inserted"))
    }

    private companion object {
        const val USERNAME = "julian"
        const val FIRST_HASH = "fake-hash-first-aaa111"
        const val SECOND_HASH = "fake-hash-second-bbb222"
    }
}
