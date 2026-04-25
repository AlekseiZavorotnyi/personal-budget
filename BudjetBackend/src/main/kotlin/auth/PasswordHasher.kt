package com.auth

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val algorithm = "PBKDF2WithHmacSHA256"
    private const val format = "pbkdf2_sha256"
    private const val iterations = 210_000
    private const val saltLengthBytes = 16
    private const val keyLengthBits = 256

    private val secureRandom = SecureRandom()
    private val base64Encoder = Base64.getEncoder()
    private val base64Decoder = Base64.getDecoder()

    fun hash(password: String): String {
        val salt = ByteArray(saltLengthBytes)
        secureRandom.nextBytes(salt)
        val hash = derive(password, salt, iterations, keyLengthBits)

        return listOf(
            format,
            iterations.toString(),
            base64Encoder.encodeToString(salt),
            base64Encoder.encodeToString(hash)
        ).joinToString("$")
    }

    fun verify(password: String, storedHash: String): Boolean {
        val parts = storedHash.split("$")
        if (parts.size != 4 || parts[0] != format) {
            return false
        }

        val storedIterations = parts[1].toIntOrNull() ?: return false
        val salt = runCatching { base64Decoder.decode(parts[2]) }.getOrNull() ?: return false
        val expectedHash = runCatching { base64Decoder.decode(parts[3]) }.getOrNull() ?: return false
        val actualHash = derive(password, salt, storedIterations, expectedHash.size * 8)

        return MessageDigest.isEqual(expectedHash, actualHash)
    }

    private fun derive(password: String, salt: ByteArray, iterations: Int, keyLengthBits: Int): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthBits)
        return SecretKeyFactory.getInstance(algorithm)
            .generateSecret(spec)
            .encoded
    }
}
