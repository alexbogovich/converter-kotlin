package com.bogovich.excel

import com.bogovich.utils.CellUtils
import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.channels.Channel
import mu.KLogging
import org.apache.poi.ss.usermodel.Cell
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

class Reader(private val mainFileChannel: Channel<RowData>, val restFileChannel: Channel<RowData>) {

    companion object : KLogging()

    suspend fun read(path: String, channel: Channel<RowData>) {
        val inputStream = Files.newInputStream(Paths.get(path))
        inputStream.use { stream ->
            read(stream, channel)
        }
    }

    suspend fun read(inputStream: InputStream, channel: Channel<RowData>) {
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
                        .associateBy { cell: Cell -> CellUtils.getCellRef(cell.columnIndex) }
                        .also { cells ->
                            logger.debug { "prepare to send row ${row.rowNum}" }
                            logger.debug { "Send $cells" }
                            channel.send(RowData(sheetNum + 1, row.rowNum, cells))
                            logger.debug { "Sent ${row.rowNum}" }
                        }
            }
        }
    }

    suspend fun readMainDoc(path: String) {
        read(path, mainFileChannel)
        mainFileChannel.close()
    }

    suspend fun readRestDocs(path: String, index: Int, total: Int) {
        read(path, restFileChannel)
        if (index == total) {
            restFileChannel.close()
        }
    }
}