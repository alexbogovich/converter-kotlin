package com.bogovich.utils

object InsuredPersonUtils {

    fun controlNumber(number: Long): String {
        return controlNumber(String.format("%09d", number))
    }

    fun controlNumber(number: String): String {
        val code = number.reversed()
                .asSequence()
                .map { it.toString().toInt() }
                .reduceIndexed { index, acc, c -> acc + c * (index + 1) }
                .run {
                    var result = this
                    while (result >= 100) {
                        if (result in 100..101)
                            result = 0
                        else
                            result %= 101
                    }
                    result
                }
        return String.format("%02d", code)
    }

    fun formattedNumber(number: Long): String {
        if (number !in 0..999_999_999) {
            throw IllegalArgumentException("Insured person must be in 0..999_999_999 but found $number")
        }
        val numberWithZeros = String.format("%09d", number)
        return numberWithZeros.substring(0, 3) +
                "-${numberWithZeros.substring(3, 6)}" +
                "-${numberWithZeros.substring(6)}" +
                " ${controlNumber(numberWithZeros)}"
    }
}