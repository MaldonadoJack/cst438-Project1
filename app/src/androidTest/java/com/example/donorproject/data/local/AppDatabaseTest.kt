package com.example.donorproject.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies the Room foundation only: that [AppDatabase] can be created and opened.
 * Uses an in-memory database so no test data survives the process.
 */
@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase

    @Before
    fun createInMemoryDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun databaseOpensWhenTheUnderlyingConnectionIsForced() {
        // databaseBuilder is lazy, so touching writableDatabase is what actually opens SQLite.
        assertTrue(database.openHelper.writableDatabase.isOpen)
        assertTrue(database.isOpen)
    }
}
