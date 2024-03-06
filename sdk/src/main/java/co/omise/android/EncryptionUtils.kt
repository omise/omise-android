package co.omise.android

import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptionUtils {
    // Computes SHA-512 hash of a string
    fun hash512(data: String): ByteArray {
        val hasher = MessageDigest.getInstance("SHA-512")
        return hasher.digest(data.toByteArray(Charsets.UTF_8))
    }

    // Decrypts the encrypted data using the decryption key and AES/CTR/NoPadding cipher
    fun aesDecrypt(
        ciphertext: ByteArray,
        key: ByteArray?,
    ): ByteArray {
        val cipher = Cipher.getInstance("AES/CTR/NoPadding")
        val keySpec = SecretKeySpec(key, "AES")

        // Extract IV from the ciphertext
        val iv = ciphertext.copyOfRange(0, 16)
        val ivParameterSpec = IvParameterSpec(iv)

        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec)

        // Decrypt ciphertext (excluding IV)
        return cipher.doFinal(ciphertext.copyOfRange(16, ciphertext.size))
    }
}
