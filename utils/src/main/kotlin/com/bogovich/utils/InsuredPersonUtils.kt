package com.bogovich.utils

object InsuredPersonUtils {
    fun controlNumber (number: String): String {
        // каждая цифра умножается на номер позиции,
        // все произведения складываются,
        var multipliesSum = 0
        (9 downTo 1).forEach { position ->
            multipliesSum += number[position - 1].toInt() * position
        }
        // потом делиться на 101
        // и еще раз делиться на 100 по модулю
        return String.format("%02d", multipliesSum % 101 % 100)
    }

    fun formattedNumber(number: Long): String {
        val numberWithZeros = String.format("%09d", number)
        return numberWithZeros.substring(0, 3) +
                "-${numberWithZeros.substring(3, 6)}" +
                "-${numberWithZeros.substring(6)}" +
                " ${controlNumber(numberWithZeros)}"
    }
}