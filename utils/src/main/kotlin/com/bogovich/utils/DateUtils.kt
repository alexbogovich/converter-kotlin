package com.bogovich.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter



object DateUtils {
    private val M_DD_YY: DateTimeFormatter = DateTimeFormatter.ofPattern("M/dd/yy")


    fun parse(s: String): LocalDate {
        return LocalDate.parse(s, M_DD_YY)
    }
}