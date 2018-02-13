package com.bogovich.utils

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

fun String.money(): BigDecimal = BigDecimalUtils.getMoney(this)
fun String.toLocalDate(): LocalDate = DateUtils.parse(this)