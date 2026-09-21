package com.example.donorproject.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoodLogDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var foodLogDao: FoodLogDao

    @Before
    fun createInMemoryDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = database.userDao()
        foodLogDao = database.foodLogDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertReturnsAGeneratedRowId() = runBlocking {
        val userId = insertUser(USERNAME)

        val rowId = foodLogDao.insert(entry(userId = userId, loggedAt = DAY_START))

        assertTrue("expected a generated row id but got $rowId", rowId > 0)
    }

    @Test
    fun findByUserAndTimeRangeReturnsThatUsersEntriesInOrder() = runBlocking {
        val userId = insertUser(USERNAME)
        val laterId = foodLogDao.insert(entry(userId = userId, foodName = "Banana", loggedAt = DAY_START + 2_000))
        val earlierId = foodLogDao.insert(entry(userId = userId, foodName = "Apple", loggedAt = DAY_START + 1_000))

        val loaded = foodLogDao.findByUserAndTimeRange(userId, DAY_START, DAY_END)

        assertEquals(listOf("Apple", "Banana"), loaded.map { it.foodName })
        assertEquals(listOf(earlierId.toInt(), laterId.toInt()), loaded.map { it.id })
    }

    @Test
    fun findByUserAndTimeRangeExcludesAnotherUsersEntries() = runBlocking {
        val ownerId = insertUser(USERNAME)
        val otherId = insertUser("other")
        foodLogDao.insert(entry(userId = ownerId, foodName = "Owner meal", loggedAt = DAY_START + 1_000))
        foodLogDao.insert(entry(userId = otherId, foodName = "Other meal", loggedAt = DAY_START + 1_000))

        val loaded = foodLogDao.findByUserAndTimeRange(ownerId, DAY_START, DAY_END)

        assertEquals(listOf("Owner meal"), loaded.map { it.foodName })
    }

    @Test
    fun findByUserAndTimeRangeUsesAHalfOpenInterval() = runBlocking {
        val userId = insertUser(USERNAME)
        foodLogDao.insert(entry(userId = userId, foodName = "Before", loggedAt = DAY_START - 1))
        foodLogDao.insert(entry(userId = userId, foodName = "Start", loggedAt = DAY_START))
        foodLogDao.insert(entry(userId = userId, foodName = "Inside", loggedAt = DAY_START + 1_000))
        foodLogDao.insert(entry(userId = userId, foodName = "End", loggedAt = DAY_END))

        val loaded = foodLogDao.findByUserAndTimeRange(userId, DAY_START, DAY_END)

        assertEquals(listOf("Start", "Inside"), loaded.map { it.foodName })
    }

    @Test
    fun deleteForUserRemovesAnOwnedEntry() = runBlocking {
        val userId = insertUser(USERNAME)
        val entryId = foodLogDao.insert(entry(userId = userId, loggedAt = DAY_START)).toInt()

        val removed = foodLogDao.deleteForUser(entryId, userId)

        assertEquals(1, removed)
        assertTrue(foodLogDao.findByUserAndTimeRange(userId, DAY_START, DAY_END).isEmpty())
    }

    @Test
    fun deleteForUserDoesNotRemoveAnotherUsersEntry() = runBlocking {
        val ownerId = insertUser(USERNAME)
        val otherId = insertUser("other")
        val entryId = foodLogDao.insert(
            entry(userId = ownerId, foodName = "Owner meal", loggedAt = DAY_START)
        ).toInt()

        val removed = foodLogDao.deleteForUser(entryId, otherId)

        assertEquals(0, removed)
        assertEquals(
            listOf("Owner meal"),
            foodLogDao.findByUserAndTimeRange(ownerId, DAY_START, DAY_END).map { it.foodName }
        )
    }

    private suspend fun insertUser(username: String): Int =
        userDao.insert(UserEntity(username = username, passwordHash = "fake-hash")).toInt()

    private fun entry(
        userId: Int,
        foodName: String = "Greek Yogurt",
        loggedAt: Long
    ): FoodLogEntity = FoodLogEntity(
        userId = userId,
        foodId = "33691",
        foodName = foodName,
        servingDescription = "170g",
        calories = 120,
        proteinGrams = 12.0,
        carbohydrateGrams = 7.0,
        fatGrams = 0.0,
        loggedAtEpochMillis = loggedAt
    )

    private companion object {
        const val USERNAME = "julian"
        const val DAY_START = 1_700_000_000_000L
        const val DAY_END = DAY_START + 86_400_000L
    }
}
