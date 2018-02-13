package com.bogovich.utils

import java.math.BigDecimal

object BigDecimalUtils {
    private val commaPattern = Regex("[,]")

    fun getMoney(value: String): BigDecimal {
/*      возможные паттерны
*       #,##
*       #.##
* */
        return BigDecimal(commaPattern.replace(value, "."))
    }
}