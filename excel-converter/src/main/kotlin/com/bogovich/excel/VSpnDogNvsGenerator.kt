package com.bogovich.excel

import com.bogovich.utils.CellUtils
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KLogging
import org.apache.poi.ss.usermodel.Cell
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.stream.XMLOutputFactory

fun main(args: Array<String>) = runBlocking {
    val channel = Channel<RowData>()
    val job = launch(coroutineContext) {
        val inputStream = Files.newInputStream(Paths.get("C:/Users/aleksandr.bogovich/Desktop/my staff/practice/converter-kotlin/excel-converter/src/main/resources/РНПФ-01.xlsx"))
        inputStream.use { stream ->
            val workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(stream)            // InputStream or File for XLSX file (required)
            for ((sheetNum, sheet) in workbook.withIndex()) {
//                println(sheet.sheetName)
                for (row in sheet) {
//                    println("read row ${row.rowNum}")
                    row.asSequence()
                            .filter { cell: Cell -> !cell.stringCellValue.isNullOrEmpty() }
                            .map { cell: Cell -> CellData.of(cell) }
                            .associateBy { cellData: CellData -> cellData.ref }
                            .also { cells ->
                                //println("prepare to send row ${row.rowNum}")
                                channel.send(RowData(sheetNum + 1, row.rowNum, cells))
                                //println("sent to send row ${row.rowNum}")
                            }
                }
            }
        }
    }

    val converter = Converter(channel)
    val out = System.out
    val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))


    writer.document {
        "ЭДПФР" tag {
            "Реквизиты" tag {
                "Дата" tag converter.cell("B3")
                "Номер" tag converter.cell("D3")
            }
            "НПФ" tag {
                "НаименованиеФормализованное" tag converter.cell("D11")
                "ИНН" tag converter.cell("D9")
            }
        }
    }

    job.cancelAndJoin()
    println("Done!")
}