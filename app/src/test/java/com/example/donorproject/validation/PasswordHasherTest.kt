package com.example.donorproject.validation

import java.util.Base64
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hashDoesNotContainThePlaintextPassword() {
        val hash = PasswordHasher.hash(PASSWORD)

        assertFalse(hash.contains(PASSWORD))
    }

    @Test
    fun verifyAcceptsTheOriginalPassword() {
        assertTrue(PasswordHasher.verify(PASSWORD, PasswordHasher.hash(PASSWORD)))
    }

    @Test
    fun verifyRejectsAWrongPassword() {
        assertFalse(PasswordHasher.verify("wrong-password", PasswordHasher.hash(PASSWORD)))
    }

    @Test
    fun verifyIsCaseSensitive() {
        assertFalse(PasswordHasher.verify(PASSWORD.uppercase(), PasswordHasher.hash(PASSWORD)))
    }

    @Test
    fun verifyRejectsAnEmptyPasswordAgainstARealHash() {
        assertFalse(PasswordHasher.verify("", PasswordHasher.hash(PASSWORD)))
    }

    /** A fresh salt per call means equal passwords must not produce equal hashes. */
    @Test
    fun theSamePasswordHashesDifferentlyEachTime() {
        assertNotEquals(PasswordHasher.hash(PASSWORD), PasswordHasher.hash(PASSWORD))
    }

    /** Short, lowercase-only passwords are valid, so they must hash and verify. */
    @Test
    fun shortLowercasePasswordRoundTrips() {
        assertTrue(PasswordHasher.verify("abc", PasswordHasher.hash("abc")))
    }

    @Test
    fun verifyRejectsZeroIterations() {
        assertFalse(PasswordHasher.verify(PASSWORD, storedHashWithIterations("0")))
    }

    @Test
    fun verifyRejectsNegativeIterations() {
        listOf("-1", "-100000", "-2147483648").forEach { iterations ->
            assertFalse(iterations, PasswordHasher.verify(PASSWORD, storedHashWithIterations(iterations)))
        }
    }

    /** An unbounded count would let a tampered row dictate an arbitrarily slow derivation. */
    @Test
    fun verifyRejectsExcessivelyLargeIterations() {
        listOf("1000001", "10000000", "2147483647").forEach { iterations ->
            assertFalse(iterations, PasswordHasher.verify(PASSWORD, storedHashWithIterations(iterations)))
        }
    }

    /** Below the floor a stored value could ask for a derivation that is too cheap. */
    @Test
    fun verifyRejectsIterationsBelowTheSupportedFloor() {
        listOf("1", "9999").forEach { iterations ->
            assertFalse(iterations, PasswordHasher.verify(PASSWORD, storedHashWithIterations(iterations)))
        }
    }

    @Test
    fun verifyRejectsMalformedIterationValues() {
        val malformed = listOf(
            "",
            " ",
            "abc",
            "100000abc",
            " 100000",
            "100000 ",
            "100_000",
            "100000.0",
            "1e5",
            "0x186a0",
            "NaN",
            "Infinity",
            "99999999999999999999"
        )

        malformed.forEach { iterations ->
            assertFalse(iterations, PasswordHasher.verify(PASSWORD, storedHashWithIterations(iterations)))
        }
    }

    @Test
    fun verifyAcceptsTheIterationCountItWrites() {
        val storedHash = PasswordHasher.hash(PASSWORD)

        // Guards the range check against excluding this object's own output.
        assertTrue(PasswordHasher.verify(PASSWORD, storedHash))
    }

    @Test
    fun verifyRejectsSaltsThatAreNotSixteenBytes() {
        listOf(0, 1, 8, 15, 17, 32).forEach { size ->
            val storedHash = storedHashWithField(SALT_FIELD, encode(ByteArray(size)))
            assertFalse("salt of $size bytes", PasswordHasher.verify(PASSWORD, storedHash))
        }
    }

    @Test
    fun verifyRejectsDerivedKeysThatAreNotThirtyTwoBytes() {
        listOf(0, 1, 16, 31, 33, 64).forEach { size ->
            val storedHash = storedHashWithField(KEY_FIELD, encode(ByteArray(size)))
            assertFalse("key of $size bytes", PasswordHasher.verify(PASSWORD, storedHash))
        }
    }

    @Test
    fun verifyRejectsMalformedStoredValuesWithoutThrowing() {
        val malformed = listOf(
            "",
            PASSWORD,
            "pbkdf2-sha256",
            "pbkdf2-sha256\$100000\$salt",
            "bcrypt\$100000\$c2FsdA\$a2V5",
            "pbkdf2-sha256\$notanumber\$c2FsdA\$a2V5",
            "pbkdf2-sha256\$100000\$not valid base64!\$a2V5"
        )

        malformed.forEach { storedHash ->
            assertFalse(storedHash, PasswordHasher.verify(PASSWORD, storedHash))
        }
    }

    /**
     * A hash this object really produced, with one field swapped out. Building it this way
     * keeps every other field valid, so a rejection can only come from the field under test.
     */
    private fun storedHashWithField(index: Int, value: String): String {
        val fields = validStoredHash.split(SEPARATOR).toMutableList()
        fields[index] = value
        return fields.joinToString(SEPARATOR)
    }

    private fun storedHashWithIterations(iterations: String): String =
        storedHashWithField(ITERATIONS_FIELD, iterations)

    private fun encode(bytes: ByteArray): String =
        Base64.getEncoder().withoutPadding().encodeToString(bytes)

    private companion object {
        const val PASSWORD = "password"
        const val SEPARATOR = "\$"
        const val ITERATIONS_FIELD = 1
        const val SALT_FIELD = 2
        const val KEY_FIELD = 3

        /** Hashing is deliberately slow, so derive one reusable valid value. */
        val validStoredHash: String by lazy { PasswordHasher.hash(PASSWORD) }
    }
}
