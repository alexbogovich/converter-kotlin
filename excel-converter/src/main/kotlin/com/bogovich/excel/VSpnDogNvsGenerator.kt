package com.bogovich.excel

import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.apache.poi.ss.usermodel.Cell
import java.nio.file.Files
import java.nio.file.Paths

fun main(args: Array<String>) = runBlocking {
    val channel = Channel<RowData>()
    val job = launch(coroutineContext) {
        val inputStream = Files.newInputStream(Paths.get("/home/alex/IdeaProjects/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))
        inputStream.use {stream ->
            val workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(stream)            // InputStream or File for XLSX file (required)
            for ((sheetNum, sheet) in workbook.withIndex()) {
                println(sheet.sheetName)
                for (row in sheet) {
                    println("read row ${row.rowNum}")
                    row.asSequence()
                            .filter { cell: Cell -> !cell.stringCellValue.isNullOrEmpty() }
                            .map { cell: Cell -> CellData.of(cell) }
                            .toList()
                            .also { cells ->
                                println("prepare to send row ${row.rowNum}")
                                channel.send(RowData(sheetNum + 1, row.rowNum, cells))
                                println("sent to send row ${row.rowNum}")
                            }
                }
            }
        }
    }
    // here we print five received integers:
    repeat(18) {
        val message = channel.receive()
        println("receive $message")
    }
    job.cancelAndJoin()
    println("Done!")
}