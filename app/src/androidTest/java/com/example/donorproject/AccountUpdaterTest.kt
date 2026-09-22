package com.example.donorproject

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.donorproject.data.local.AccountDao
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.PasswordHasher
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountUpdaterTest {

    private lateinit var database: AppDatabase
    private lateinit var userDao: UserDao
    private lateinit var accountDao: AccountDao
    private lateinit var originalUser: UserEntity
    private var userId: Int = 0

    @Before
    fun setUp() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()

        userDao = database.userDao()
        accountDao = database.accountDao()

        userId = userDao.insert(
            UserEntity(
                username = "daniel",
                passwordHash = PasswordHasher.hash("original-password")
            )
        ).toInt()

        originalUser = requireNotNull(accountDao.findById(userId))
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun changingUsernamePreservesIdAndPasswordAndUpdatesLogin() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "  daniel_updated  ",
            newPassword = "",
            confirmPassword = "",
            accountDao = accountDao
        )

        assertEquals(
            AccountUpdateOutcome.Updated("daniel_updated"),
            outcome
        )

        val saved = requireNotNull(accountDao.findById(userId))
        assertEquals(userId, saved.id)
        assertEquals("daniel_updated", saved.username)
        assertEquals(originalUser.passwordHash, saved.passwordHash)
        assertNull(userDao.findByUsername("daniel"))

        assertEquals(
            LoginOutcome.Success(userId, "daniel_updated"),
            AccountAuthenticator.logIn(
                "daniel_updated",
                "original-password",
                userDao
            )
        )

        assertTrue(
            AccountAuthenticator.logIn(
                "daniel",
                "original-password",
                userDao
            ) is LoginOutcome.Rejected
        )
    }

    @Test
    fun changingPasswordStoresHashAndRejectsOldPassword() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "daniel",
            newPassword = "new-password",
            confirmPassword = "new-password",
            accountDao = accountDao
        )

        assertTrue(outcome is AccountUpdateOutcome.Updated)

        val saved = requireNotNull(accountDao.findById(userId))
        assertEquals(userId, saved.id)
        assertEquals("daniel", saved.username)
        assertNotEquals("new-password", saved.passwordHash)
        assertNotEquals(originalUser.passwordHash, saved.passwordHash)
        assertTrue(PasswordHasher.verify("new-password", saved.passwordHash))

        assertEquals(
            LoginOutcome.Success(userId, "daniel"),
            AccountAuthenticator.logIn("daniel", "new-password", userDao)
        )

        assertTrue(
            AccountAuthenticator.logIn(
                "daniel",
                "original-password",
                userDao
            ) is LoginOutcome.Rejected
        )
    }

    @Test
    fun changingUsernameAndPasswordTogetherUpdatesLogin() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "new_name",
            newPassword = "new-password",
            confirmPassword = "new-password",
            accountDao = accountDao
        )

        assertEquals(AccountUpdateOutcome.Updated("new_name"), outcome)

        assertEquals(
            LoginOutcome.Success(userId, "new_name"),
            AccountAuthenticator.logIn("new_name", "new-password", userDao)
        )
        assertNull(userDao.findByUsername("daniel"))
    }

    @Test
    fun duplicateUsernameDoesNotChangeEitherAccount() = runBlocking {
        val otherId = userDao.insert(
            UserEntity(
                username = "julian",
                passwordHash = PasswordHasher.hash("other-password")
            )
        ).toInt()

        val otherBefore = accountDao.findById(otherId)

        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "  julian  ",
            newPassword = "new-password",
            confirmPassword = "new-password",
            accountDao = accountDao
        )

        assertTrue(outcome is AccountUpdateOutcome.Rejected)
        assertNotNull(
            (outcome as AccountUpdateOutcome.Rejected).errors.usernameError
        )
        assertEquals(originalUser, accountDao.findById(userId))
        assertEquals(otherBefore, accountDao.findById(otherId))
    }

    @Test
    fun blankUsernameDoesNotSaveChanges() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "   ",
            newPassword = "",
            confirmPassword = "",
            accountDao = accountDao
        )

        assertTrue(outcome is AccountUpdateOutcome.Rejected)
        assertNotNull(
            (outcome as AccountUpdateOutcome.Rejected).errors.usernameError
        )
        assertEquals(originalUser, accountDao.findById(userId))
    }

    @Test
    fun mismatchedPasswordsDoNotSaveUsernameOrPassword() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "new_name",
            newPassword = "new-password",
            confirmPassword = "different-password",
            accountDao = accountDao
        )

        assertTrue(outcome is AccountUpdateOutcome.Rejected)
        assertNotNull(
            (outcome as AccountUpdateOutcome.Rejected)
                .errors.confirmPasswordError
        )
        assertEquals(originalUser, accountDao.findById(userId))
        assertNull(userDao.findByUsername("new_name"))
    }

    @Test
    fun whitespacePasswordIsRejected() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = userId,
            username = "daniel",
            newPassword = "   ",
            confirmPassword = "   ",
            accountDao = accountDao
        )

        assertTrue(outcome is AccountUpdateOutcome.Rejected)
        assertNotNull(
            (outcome as AccountUpdateOutcome.Rejected).errors.passwordError
        )
        assertEquals(originalUser, accountDao.findById(userId))
    }

    @Test
    fun missingUserDoesNotCreateAnAccount() = runBlocking {
        val outcome = AccountUpdater.updateAccount(
            userId = -1,
            username = "missing_user",
            newPassword = "new-password",
            confirmPassword = "new-password",
            accountDao = accountDao
        )

        assertEquals(AccountUpdateOutcome.UserNotFound, outcome)
        assertNull(userDao.findByUsername("missing_user"))
        assertEquals(originalUser, accountDao.findById(userId))
    }
}