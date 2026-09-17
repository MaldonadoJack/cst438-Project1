package com.example.donorproject.validation

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Derives storable password hashes with PBKDF2, using only the Java platform so no new
 * dependency is required.
 *
 * [hash] returns a single self-describing string that carries the algorithm, iteration
 * count, and per-password salt alongside the derived key, so [verify] needs nothing but
 * the stored value. Keeping the salt inside that string is what lets this live in the
 * existing `users.passwordHash` column without a schema change.
 *
 * Both functions block for a noticeable amount of time by design, so call them from a
 * background dispatcher rather than the main thread.
 */
object PasswordHasher {

    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val SCHEME = "pbkdf2-sha256"
    private const val SEPARATOR = '$'
    private const val FIELD_COUNT = 4
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    private const val KEY_LENGTH_BYTES = KEY_LENGTH_BITS / 8
    private const val SALT_LENGTH_BYTES = 16

    /**
     * Bounds accepted by [verify]. The floor keeps a tampered stored value from asking for
     * a cheap derivation, and the ceiling keeps one from asking for an arbitrarily
     * expensive one. [ITERATIONS] has to stay inside this range.
     */
    private const val MIN_ITERATIONS = 10_000
    private const val MAX_ITERATIONS = 1_000_000

    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH_BYTES).also { SecureRandom().nextBytes(it) }
        val key = deriveKey(password, salt, ITERATIONS)

        return listOf(
            SCHEME,
            ITERATIONS.toString(),
            encode(salt),
            encode(key)
        ).joinToString(SEPARATOR.toString())
    }

    /**
     * Returns false rather than throwing when [storedHash] is not a value this object
     * produced, so a row written by an older build cannot crash a sign-in attempt.
     *
     * Every field is checked before any key is derived. [PBEKeySpec] itself throws on a
     * nonpositive iteration count or an empty salt, and a stored value is untrusted input,
     * so validating first is what keeps those cases a plain `false`.
     */
    fun verify(password: String, storedHash: String): Boolean {
        val fields = storedHash.split(SEPARATOR)
        if (fields.size != FIELD_COUNT || fields[0] != SCHEME) {
            return false
        }

        val iterations = fields[1].toIntOrNull() ?: return false
        if (iterations < MIN_ITERATIONS || iterations > MAX_ITERATIONS) {
            return false
        }

        val salt = decode(fields[2]) ?: return false
        if (salt.size != SALT_LENGTH_BYTES) {
            return false
        }

        val expectedKey = decode(fields[3]) ?: return false
        if (expectedKey.size != KEY_LENGTH_BYTES) {
            return false
        }

        // Length-independent comparison, unlike String equality on the encoded form.
        return MessageDigest.isEqual(expectedKey, deriveKey(password, salt, iterations))
    }

    private fun deriveKey(password: String, salt: ByteArray, iterations: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH_BITS)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun encode(bytes: ByteArray): String =
        Base64.getEncoder().withoutPadding().encodeToString(bytes)

    private fun decode(value: String): ByteArray? =
        try {
            Base64.getDecoder().decode(value)
        } catch (exception: IllegalArgumentException) {
            null
        }
}
