package com.example.donorproject

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Drives the real screen against a real in-memory database. The point of these cases is
 * what happens after a successful save: the account exists, the confirmation is visible,
 * and nothing navigates on its own.
 */
@RunWith(AndroidJUnit4::class)
class SignUpScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao

    private var goToLoginClicks = 0
    private var backClicks = 0

    @Before
    fun createInMemoryDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = database.userDao()
        goToLoginClicks = 0
        backClicks = 0
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun successfulSignUpSavesTheAccountAndConfirmsItWithoutNavigating() {
        showSignUpScreen()

        submit(username = USERNAME, password = PASSWORD, confirmPassword = PASSWORD)

        waitForText(SIGN_UP_SUCCESS_MESSAGE)
        composeTestRule.onNodeWithText(SIGN_UP_SUCCESS_MESSAGE).assertIsDisplayed()

        assertNotNull(runBlocking { userDao.findByUsername(USERNAME) })

        // Nothing left this screen: reaching the logged-in part of the app stays a
        // deliberate user action.
        assertEquals(0, goToLoginClicks)
        assertEquals(0, backClicks)

        // The signup form is still the thing on screen.
        composeTestRule.onNodeWithText("Username").assertIsDisplayed()
    }

    @Test
    fun successfulSignUpOffersLoginOnlyWhenTheUserAsksForIt() {
        showSignUpScreen()

        submit(username = USERNAME, password = PASSWORD, confirmPassword = PASSWORD)
        waitForText(SIGN_UP_SUCCESS_MESSAGE)

        assertEquals(0, goToLoginClicks)

        composeTestRule.onNodeWithText("Go to Login").performClick()
        composeTestRule.waitForIdle()

        assertEquals(1, goToLoginClicks)
    }

    @Test
    fun duplicateUsernameShowsAnErrorAndSavesNothingNew() {
        val existingHash = "existing-account-hash"
        runBlocking { userDao.insert(UserEntity(username = USERNAME, passwordHash = existingHash)) }

        showSignUpScreen()

        submit(username = USERNAME, password = PASSWORD, confirmPassword = PASSWORD)

        waitForText(ValidationError.USERNAME_TAKEN.message)
        assertNoSuccessConfirmation()

        assertEquals(existingHash, runBlocking { userDao.findByUsername(USERNAME) }?.passwordHash)
        assertEquals(0, goToLoginClicks)
        assertEquals(0, backClicks)
    }

    @Test
    fun mismatchedPasswordsShowAnErrorAndSaveNothing() {
        showSignUpScreen()

        submit(
            username = USERNAME,
            password = PASSWORD,
            confirmPassword = "not-the-same-password"
        )

        waitForText(ValidationError.PASSWORD_MISMATCH.message)
        assertNoSuccessConfirmation()

        assertNull(runBlocking { userDao.findByUsername(USERNAME) })
        assertEquals(0, goToLoginClicks)
        assertEquals(0, backClicks)
    }

    private fun showSignUpScreen() {
        composeTestRule.setContent {
            SignUpScreen(
                userDao = userDao,
                onGoToLoginClick = { goToLoginClicks++ },
                onBackClick = { backClicks++ }
            )
        }
    }

    private fun submit(username: String, password: String, confirmPassword: String) {
        composeTestRule.onNodeWithText("Username").performTextInput(username)
        composeTestRule.onNodeWithText("Password").performTextInput(password)
        composeTestRule.onNodeWithText("Confirm Password").performTextInput(confirmPassword)

        // The button carries the same label as the heading, so pick the clickable one.
        composeTestRule.onAllNodesWithText("Sign Up")
            .filterToOne(hasClickAction())
            .performClick()
    }

    private fun waitForText(text: String) {
        composeTestRule.waitUntil(timeoutMillis = TIMEOUT_MILLIS) {
            composeTestRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun assertNoSuccessConfirmation() {
        assertFalse(
            composeTestRule.onAllNodesWithText(SIGN_UP_SUCCESS_MESSAGE)
                .fetchSemanticsNodes()
                .isNotEmpty()
        )
    }

    private companion object {
        const val USERNAME = "julian"
        const val PASSWORD = "password"
        const val TIMEOUT_MILLIS = 10_000L
    }
}
