package com.example.beginnerfit

import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptDecryptManager {
    private const val SECRET_KEY = "QAZPLMWSCOKNEDC$"
    private const val SECRET_IV = "QAZPLMWSCOKNED!!"

    fun String.encryptCBC(): String {
        val iv = IvParameterSpec(SECRET_IV.toByteArray())
        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
        val crypted = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.getEncoder().encode(crypted)
        return String(encodedByte)
    }

//    fun String.decryptCBC(): String {
//        val decodedByte: ByteArray = Base64.getDecoder().decode(this)
//        val iv = IvParameterSpec(SECRET_IV.toByteArray())
//        val keySpec = SecretKeySpec(SECRET_KEY.toByteArray(), "AES")
//        val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
//        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv)
//        val output = cipher.doFinal(decodedByte)
//        return String(output)
//    }

     fun getEncryptedFileName(email: String): String {
        return email.encryptCBC()
            .replace("/", "_")
            .replace("+", "-")
            .replace("=", "") + ".json"
    }
}