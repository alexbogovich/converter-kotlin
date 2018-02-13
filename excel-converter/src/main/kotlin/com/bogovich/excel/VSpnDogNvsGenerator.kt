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

    val converter = ConverterV2(channel)
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

class ConverterV2(val channel: Channel<RowData>) {
    companion object : KLogging()

    val metaData = mutableMapOf<String, String>()
    private lateinit var rowData: RowData
    var sheetNum: Int = 1
    var rowNum: Int = 0

    suspend fun cell(ref: String, sheet: Int = 1): String {
//        logger.info { "request cell $ref $sheet" }
        val cellRowNum: Int = CellUtils.getRowNum(ref)
//        logger.info { "get cellRowNum $cellRowNum" }
        while (/*metaData.containsKey("$ref#$sheet") || */rowNum < cellRowNum) {
//            logger.info { "request read row ${rowNum + 1}" }
            readToMeta()
        }
        return metaData["$ref#$sheet"].toString()
    }

    suspend fun readToMeta() {
        readRowData()
        rowData.data.forEach { ref, value -> metaData["$ref${rowData.rowNum + 1}#${rowData.sheetNum}"] = value.data }
//        logger.info { "meta = $metaData" }
    }

    suspend fun readRowData() {
        rowData = channel.receive()
//        logger.info { "reseave row $rowData" }
        rowNum = rowData.rowNum
    }

    suspend fun getRow(): RowData {
        if (!this::rowData.isInitialized) {
            readRowData()
        }
        return rowData
    }
}