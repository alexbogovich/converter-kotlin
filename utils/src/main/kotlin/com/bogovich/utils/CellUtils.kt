package com.bogovich.utils

object CellUtils {
    private val alphabet = ('A'..'Z').toList()
    private val len = alphabet.size
    private val numRegex = Regex("[^\\d]")

    fun getCellRef(column:Int):String {
        var result = ""
        var value = column
        while (value >= 0) {
            result = alphabet[value % len] + result
            value /= len
            value--
        }
        return result
    }

    fun getRowNum(ref: String): Int {
        return numRegex.replace(ref, "").toInt()
    }
}