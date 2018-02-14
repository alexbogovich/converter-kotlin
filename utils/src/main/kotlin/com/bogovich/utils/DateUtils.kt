package com.bogovich.utils

import mu.KotlinLogging
import java.time.LocalDate
import java.time.format.DateTimeFormatter


object DateUtils {
    private val logger = KotlinLogging.logger {}

    private val M_DD_YY: DateTimeFormatter = DateTimeFormatter.ofPattern("M/dd/yy")
    // TODO паттерн 1/15/2017

    fun parse(s: String): LocalDate {
        logger.info { "Try parse $s" }
        return LocalDate.parse(s, M_DD_YY)
    }
}