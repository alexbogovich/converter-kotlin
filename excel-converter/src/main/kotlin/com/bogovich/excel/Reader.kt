package com.bogovich.excel

import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.channels.Channel
import mu.KLogging
import org.apache.poi.ss.usermodel.Cell
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class Reader (private val mainFileChannel: Channel<RowData>, private val restFileChannel: Channel<RowData>) {

    companion object : KLogging()

    suspend fun readMainDoc(path: String) {
        val inputStream = Files.newInputStream(Paths.get(path))
        inputStream.use { stream ->
            readMainDoc(stream)
        }
        mainFileChannel.close()
    }

    suspend fun readMainDoc(inputStream: InputStream) {
        val workbook = StreamingReader.builder()
                .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                .open(inputStream)            // InputStream or File for XLSX file (required)
        for ((sheetNum, sheet) in workbook.withIndex()) {
//                println(sheet.sheetName)
            for (row in sheet) {
//                    println("read row ${row.rowNum}")
                row.asSequence()
                        .filter { cell: Cell -> !cell.stringCellValue.isNullOrEmpty() }
                        .map { cell: Cell -> CellData.of(cell) }
                        .associateBy { cellData: CellData -> cellData.ref }
                        .also { cells ->
                            logger.info { "prepare to send row ${row.rowNum}" }
                            logger.info { "Send $cells" }
                            mainFileChannel.send(RowData(sheetNum + 1, row.rowNum, cells))
                            logger.info { "Sent ${row.rowNum}" }
                        }
            }
        }
    }

    suspend fun readRestDocs(path: String) {
        val inputStream = Files.newInputStream(Paths.get(path))
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
                                logger.info { "prepare to send row ${row.rowNum}" }
                                logger.info { "Send $cells" }
                                restFileChannel.send(RowData(sheetNum + 1, row.rowNum, cells))
                                logger.info { "Sent ${row.rowNum}" }
                            }
                }
            }
        }
    }
}