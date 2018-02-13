package com.bogovich.utils

import java.math.BigDecimal

object BigDecimalUtils {
    private val quotePattern = Regex("[\"]")
    private val commaPattern = Regex("[,]")

    fun getMoney(value: String): BigDecimal {
/*      возможные паттерны
*       #,##
*       #.##
*       "#,##"
*       "#.##"
* */
        var result = quotePattern.replace(value, "")
        result = commaPattern.replace(result, ".")
        return BigDecimal(result)
    }
}