package com.example.donorproject

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.donorproject.data.local.AppDatabase
import com.example.donorproject.data.local.UserDao
import com.example.donorproject.data.local.UserEntity
import com.example.donorproject.validation.PasswordHasher
import com.example.donorproject.validation.ValidationError
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Exercises account creation against a real Room database (in memory, one per test), so
 * the unique index and the actual insert are part of what is under test rather than being
 * stubbed out.
 */
@RunWith(AndroidJUnit4::class)
class AccountCreatorTest {

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
    fun validCredentialsSaveTheAccount() = runBlocking {
        val outcome = AccountCreator.createAccount(
            username = USERNAME,
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = userDao
        )

        assertEquals(SignUpOutcome.Created(USERNAME), outcome)

        val saved = userDao.findByUsername(USERNAME)
        assertNotNull(saved)
        assertEquals(USERNAME, saved?.username)
    }

    @Test
    fun savedPasswordIsHashedRatherThanStoredAsPlaintext() = runBlocking {
        AccountCreator.createAccount(
            username = USERNAME,
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = userDao
        )

        val storedHash = userDao.findByUsername(USERNAME)?.passwordHash
        assertNotNull(storedHash)
        assertFalse(storedHash == PASSWORD)
        assertFalse(storedHash!!.contains(PASSWORD))
        assertTrue(PasswordHasher.verify(PASSWORD, storedHash))
    }

    @Test
    fun duplicateUsernameIsRejectedAndDoesNotOverwriteTheExistingAccount() = runBlocking {
        val firstHash = "first-account-hash"
        userDao.insert(UserEntity(username = USERNAME, passwordHash = firstHash))

        val outcome = AccountCreator.createAccount(
            username = USERNAME,
            password = "a-different-password",
            confirmPassword = "a-different-password",
            userDao = userDao
        )

        assertRejectedWith(ValidationError.USERNAME_TAKEN, outcome)
        assertEquals(firstHash, userDao.findByUsername(USERNAME)?.passwordHash)
    }

    /**
     * The case that used to crash: validation saw the raw string while the insert saw the
     * trimmed one, so the unique index rejected a username validation had accepted.
     */
    @Test
    fun untrimmedDuplicateUsernameIsRejectedWithoutCrashing() = runBlocking {
        userDao.insert(UserEntity(username = USERNAME, passwordHash = "existing-hash"))

        val outcome = AccountCreator.createAccount(
            username = "  $USERNAME  ",
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = userDao
        )

        assertRejectedWith(ValidationError.USERNAME_TAKEN, outcome)
    }

    @Test
    fun usernameIsTrimmedBeforeBeingSaved() = runBlocking {
        val outcome = AccountCreator.createAccount(
            username = "  $USERNAME  ",
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = userDao
        )

        assertEquals(SignUpOutcome.Created(USERNAME), outcome)
        assertNotNull(userDao.findByUsername(USERNAME))
        assertNull(userDao.findByUsername("  $USERNAME  "))
    }

    @Test
    fun mismatchedPasswordsSaveNothing() = runBlocking {
        val outcome = AccountCreator.createAccount(
            username = USERNAME,
            password = PASSWORD,
            confirmPassword = "not-the-same-password",
            userDao = userDao
        )

        assertRejectedWith(ValidationError.PASSWORD_MISMATCH, outcome)
        assertNull(userDao.findByUsername(USERNAME))
    }

    @Test
    fun blankUsernameSavesNothing() = runBlocking {
        val outcome = AccountCreator.createAccount(
            username = "   ",
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = userDao
        )

        assertRejectedWith(ValidationError.USERNAME_BLANK, outcome)
        assertNull(userDao.findByUsername("   "))
    }

    @Test
    fun blankPasswordSavesNothing() = runBlocking {
        val outcome = AccountCreator.createAccount(
            username = USERNAME,
            password = "",
            confirmPassword = "",
            userDao = userDao
        )

        assertRejectedWith(ValidationError.PASSWORD_BLANK, outcome)
        assertNull(userDao.findByUsername(USERNAME))
    }

    /**
     * Validation cannot see a username claimed after its own check, so the insert itself
     * has to stay safe. This DAO reproduces that race directly.
     */
    @Test
    fun constraintViolationOnInsertIsReportedAsATakenUsername() = runBlocking {
        val racingDao = object : UserDao {
            override suspend fun insert(user: UserEntity): Long =
                throw SQLiteConstraintException("UNIQUE constraint failed: users.username")

            override suspend fun findByUsername(username: String): UserEntity? = null
        }

        val outcome = AccountCreator.createAccount(
            username = USERNAME,
            password = PASSWORD,
            confirmPassword = PASSWORD,
            userDao = racingDao
        )

        assertRejectedWith(ValidationError.USERNAME_TAKEN, outcome)
    }

    private fun assertRejectedWith(expected: ValidationError, outcome: SignUpOutcome) {
        assertTrue("Expected a rejection but got $outcome", outcome is SignUpOutcome.Rejected)

        val errors = (outcome as SignUpOutcome.Rejected).errors
        val reported = listOfNotNull(
            errors.usernameError,
            errors.passwordError,
            errors.confirmPasswordError
        )

        assertTrue(
            "Expected ${expected.message} but got $reported",
            expected.message in reported
        )
    }

    private companion object {
        const val USERNAME = "julian"
        const val PASSWORD = "password"
    }
}
