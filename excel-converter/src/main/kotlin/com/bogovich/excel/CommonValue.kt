package com.bogovich.excel

import java.time.LocalDateTime
import java.util.*

data class CommonValue(val guid: UUID = UUID.randomUUID(),
                       val localDateTime: LocalDateTime = LocalDateTime.now())