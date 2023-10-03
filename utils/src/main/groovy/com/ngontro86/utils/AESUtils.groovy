package com.ngontro86.utils

import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom

class AESUtils {
    private static class RandomPrivate {
        static def random = new SecureRandom()
    }

    private static byte[] hash(byte[] salt, String key) {
        final def spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 128)
        final def keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
        keyFactory.generateSecret(spec).getEncoded()
    }

    static String encrypt(String key, String plainText) {
        final byte[] salt = new byte[16]
        RandomPrivate.random.nextBytes(salt)
        final def ivBytes = new byte[16]
        RandomPrivate.random.nextBytes(ivBytes)
        final def iv = new IvParameterSpec(ivBytes)
        final def cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(hash(salt, key), "AES"), iv)
        final def encValue = cipher.doFinal(plainText.getBytes())

        final def finalCiphertext = new byte[encValue.length + 2 * 16]
        System.arraycopy(ivBytes, 0, finalCiphertext, 0, 16)
        System.arraycopy(salt, 0, finalCiphertext, 16, 16)
        System.arraycopy(encValue, 0, finalCiphertext, 32, encValue.length)

        return Base64.getEncoder().encodeToString(finalCiphertext)
    }

    static String decrypt(String key, String encryptedText) {
        final encryptedStr = Base64.getDecoder().decode(encryptedText)
        final def salt = new byte[16]
        final def ivBytes = new byte[16]
        final def decryptedText = new byte[encryptedStr.length - 2 * 16]
        System.arraycopy(encryptedStr, 0, ivBytes, 0, 16)
        System.arraycopy(encryptedStr, 16, salt, 0, 16)
        System.arraycopy(encryptedStr, 32, decryptedText, 0, decryptedText.length)

        def iv = new IvParameterSpec(ivBytes)
        final def cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(hash(salt, key), "AES"), iv)

        new String(cipher.doFinal(decryptedText))
    }

}
