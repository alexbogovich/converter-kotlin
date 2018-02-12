package com.bogovich.excel

import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import mu.KLogging
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.coroutines.experimental.CoroutineContext


fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}

data class Sum(var sum: BigDecimal = BigDecimal.ZERO, var id: BigDecimal = BigDecimal.ZERO)

class Reader {

    companion object : KLogging()

    fun fizz(context: CoroutineContext) = produce<String>(context) {
        while (true) { // sends "Fizz" every 300 ms
            delay(50)
            send("Fizz")
        }
    }

    fun read() {
        val inputStream = Files.newInputStream(Paths.get("/home/alex/IdeaProjects/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))

        inputStream.use {
            val workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(it)            // InputStream or File for XLSX file (required)

            workbook.asSequence()
                    .flatMap { sheet: Sheet ->  sheet.asSequence() }
                    .flatMap { row: Row ->  row.asSequence() }
                    .filterNot {
                        it.stringCellValue.isNullOrEmpty()
                    }
                    .forEach { logger.info { "column ${it.columnIndex} value ${it.stringCellValue}" } }
        }
    }
}