package net.darkmeow.irc.utils

import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AESUtils {
    fun encryptAES(input: String, key: String): String {
        val secretKey = SecretKeySpec(key.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        return Base64.getEncoder().encodeToString(cipher.doFinal(input.toByteArray()))
    }

    fun decryptAES(input: String, key: String): String? {
        return try {
            val cipher = Cipher.getInstance("AES").apply {
                val secretKey = SecretKeySpec(key.toByteArray(), "AES")
                init(Cipher.DECRYPT_MODE, secretKey)
            }

            Base64.getDecoder().decode(input).let { decodedBytes ->
                String(cipher.doFinal(decodedBytes))
            }
        } catch (e: Exception) {
            null
        }
    }

}