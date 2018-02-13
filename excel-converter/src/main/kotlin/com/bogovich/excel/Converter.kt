package com.bogovich.excel

import com.bogovich.utils.CellUtils
import kotlinx.coroutines.experimental.channels.Channel
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

    suspend fun stream(startCondition: suspend (converter:Converter) -> Boolean,
               endCondition: suspend (converter:Converter) -> Boolean,
               proccess: suspend (row: RowData) -> Unit) {
        if (state == ReadState.META) {
            while (!startCondition(this)) {
                readToMeta()
            }
            state = ReadState.STREAM
            if (startCondition(this) && !endCondition(this)) {
                do {
                    proccess(rowData)
                    readRowData()
                } while (startCondition(this) && !endCondition(this))
            }
            state = ReadState.META
        }
    }

    enum class ReadState {
        META,
        STREAM
    }
}