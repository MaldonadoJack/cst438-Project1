package com.example.donorproject

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomePageIntentTest {

    @Test
    fun createIntentStoresTheAuthenticatedUserId() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = HomePage.createIntent(context, USER_ID)

        assertEquals(USER_ID, HomePage.userIdFromIntent(intent))
        assertTrue(HomePage.isValidUserId(HomePage.userIdFromIntent(intent)))
    }

    @Test
    fun anIntentWithoutAUserIdIsRejected() {
        val userId = HomePage.userIdFromIntent(Intent())

        assertFalse(HomePage.isValidUserId(userId))
    }

    private companion object {
        const val USER_ID = 7
    }
}
