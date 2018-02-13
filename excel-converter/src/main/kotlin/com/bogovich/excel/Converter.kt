package com.bogovich.excel

import com.bogovich.utils.CellUtils
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.withTimeout
import mu.KLogging

class Converter(val channel: Channel<RowData>) {
    companion object : KLogging()

    val metaData = mutableMapOf<String, String>()
    private lateinit var rowData: RowData
    var sheetNum: Int = 1
    var rowNum: Int = 0
    var state: ReadState = ReadState.META

    suspend fun cell(ref: String, sheet: Int = 1): String {
//        logger.info { "request cell $ref $sheet" }
        val cellRowNum: Int = CellUtils.getRowNum(ref)
//        logger.info { "get cellRowNum $cellRowNum" }
        while (/*metaData.containsKey("$ref#$sheet") || */rowNum < cellRowNum) {
//            logger.info { "request read row ${rowNum + 1}" }
            saveToMeta(readRowData())
        }
        return metaData["$ref#$sheet"].toString()
    }

    fun saveToMeta(row: RowData) {
        row.data.forEach { ref, value -> metaData["$ref${rowData.rowNum + 1}#${rowData.sheetNum}"] = value.data }
//        logger.info { "meta = $metaData" }
    }




    suspend fun readRowData(): RowData {
        withTimeout(5000) {
            rowData = channel.receive()
//        logger.info { "reseave row $rowData" }
            rowNum = rowData.rowNum
        }
        return rowData
    }

    suspend fun readRestToMeta() {
        logger.info { "current meta = $metaData" }
        logger.info { "try readRestToMeta" }

//        for (data in channel) {
//            logger.info { "receive rest data = $data" }
//            data.data.forEach { ref, value -> metaData["$ref${rowData.rowNum + 1}#${rowData.sheetNum}"] = value.data }
//        }

        readRowData()

//        channel.close()
        logger.info { "end readRestToMeta" }
    }

    suspend fun getRow(): RowData {
        if (!this::rowData.isInitialized) {
            readRowData()
        }
        return rowData
    }

    suspend fun stream(startCondition: (rowData:RowData) -> Boolean,
               endCondition: (rowData:RowData) -> Boolean,
               proccess: suspend (row: RowData) -> Unit) {
        if (state == ReadState.META) {
            while (!startCondition(readRowData())) {
                saveToMeta(rowData)
            }
            state = ReadState.STREAM
            if (startCondition(rowData) && !endCondition(rowData)) {
                do {
                    proccess(rowData)
                    readRowData()
                } while (startCondition(rowData) && !endCondition(rowData))
                saveToMeta(rowData)
                readRestToMeta()
            }
            state = ReadState.META
        }
    }

    enum class ReadState {
        META,
        STREAM
    }
}