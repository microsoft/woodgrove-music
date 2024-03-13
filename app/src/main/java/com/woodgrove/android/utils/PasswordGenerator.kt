package com.woodgrove.android.utils

import kotlin.random.Random

object PasswordGenerator {
    private const val LOWER_CASE = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SPECIAL_CHARS = "!@#$%^&*()-_+=<>?/."

    fun generatePassword(
        length: Int,
        includeLowerCase: Boolean = true,
        includeUpperCase: Boolean = true,
        includeDigits: Boolean = true,
        includeSpecialChars: Boolean = false
    ): String {
        val charPool = StringBuilder()
        val passwordChars = mutableListOf<Char>()

        if (includeLowerCase) charPool.append(LOWER_CASE)
        if (includeUpperCase) charPool.append(UPPER_CASE)
        if (includeDigits) charPool.append(DIGITS)
        if (includeSpecialChars) charPool.append(SPECIAL_CHARS)

        if (charPool.isEmpty()) {
            throw IllegalArgumentException("At least one character set should be included.")
        }

        // Add at least one character from each character set
        if (includeLowerCase) passwordChars.add(LOWER_CASE.random())
        if (includeUpperCase) passwordChars.add(UPPER_CASE.random())
        if (includeDigits) passwordChars.add(DIGITS.random())

        // Fill the remaining characters randomly
        repeat(length - passwordChars.size) {
            passwordChars.add(charPool.random())
        }

        // Shuffle the password characters
        passwordChars.shuffle()

        return passwordChars.joinToString("")
    }
}