package com.example.donorproject.data.local

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Builds a version-1 `users` database by hand, then opens it with the real
 * [AppDatabase] v2 builder so [AppDatabase.MIGRATION_1_2] is what upgrades it.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseMigrationTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private var roomDatabase: AppDatabase? = null

    @After
    fun tearDown() {
        roomDatabase?.close()
        context.deleteDatabase(DATABASE_NAME)
    }

    @Test
    fun migratingFromVersion1PreservesUsersAndCreatesTheFoodLogTable() {
        context.deleteDatabase(DATABASE_NAME)
        createVersion1DatabaseWithUser()

        val migrated = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
            .also { roomDatabase = it }

        val stored = requireNotNull(
            kotlinx.coroutines.runBlocking {
                migrated.userDao().findByUsername(USERNAME)
            }
        ) { "migrated database should still contain $USERNAME" }

        assertEquals(USERNAME, stored.username)
        assertEquals(STORED_HASH, stored.passwordHash)

        val insertedLogId = kotlinx.coroutines.runBlocking {
            migrated.foodLogDao().insert(
                FoodLogEntity(
                    userId = stored.id,
                    foodId = "33691",
                    foodName = "Greek Yogurt",
                    servingDescription = "170g",
                    calories = 120,
                    proteinGrams = 12.0,
                    carbohydrateGrams = 7.0,
                    fatGrams = 0.0,
                    loggedAtEpochMillis = 1_700_000_000_000L
                )
            )
        }

        assertTrue(insertedLogId > 0)

        val loaded = kotlinx.coroutines.runBlocking {
            migrated.foodLogDao().findByUserAndTimeRange(
                userId = stored.id,
                startInclusive = 1_700_000_000_000L,
                endExclusive = 1_700_000_000_000L + 1
            )
        }

        assertEquals(1, loaded.size)
        assertEquals("Greek Yogurt", loaded.single().foodName)
    }

    private fun createVersion1DatabaseWithUser() {
        val configuration = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(DATABASE_NAME)
            .callback(object : SupportSQLiteOpenHelper.Callback(1) {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `users` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `username` TEXT NOT NULL,
                            `passwordHash` TEXT NOT NULL
                        )
                        """.trimIndent()
                    )
                    db.execSQL(
                        "CREATE UNIQUE INDEX IF NOT EXISTS `index_users_username` ON `users` (`username`)"
                    )
                }

                override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                    error("version 1 helper should not upgrade")
                }
            })
            .build()

        FrameworkSQLiteOpenHelperFactory().create(configuration).writableDatabase.use { db ->
            db.execSQL(
                "INSERT INTO `users` (`username`, `passwordHash`) VALUES (?, ?)",
                arrayOf(USERNAME, STORED_HASH)
            )
        }
    }

    private companion object {
        const val DATABASE_NAME = "migration-1-2-test.db"
        const val USERNAME = "julian"
        const val STORED_HASH = "preserved-hash"
    }
}
