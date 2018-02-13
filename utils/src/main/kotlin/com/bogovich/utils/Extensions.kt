package com.bogovich.utils

import java.math.BigDecimal

fun String.money(): BigDecimal = BigDecimalUtils.getMoney(this)