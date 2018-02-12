package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KLogging

class Converter(val xmlWriter: DslXMLStreamWriter) {

    companion object : KLogging()

    fun abc(workSheetHandler: ExcelWorkSheetHandler) = runBlocking<Unit> {
        val channel = Channel<RowData>()
        launch {
//             this might be heavy CPU-consuming computation or async logic, we'll just send five squares
            workSheetHandler.rowEndCallback = {
                rowData: RowData ->
                channel.send(rowData)
            }
//            channel.send(RowData(1, 2, mutableListOf()))
        }
        // here we print five received integers:
        var i = 0
        val mutableMapOf = mutableMapOf<Int, Int>()
        repeat(100) {
            if (i % 2 == 0) {
                println("do some staff")
            }
            val list = channel.receive()
            //mutableMapOf.putAll(list)
            println(mutableMapOf)
            //delay(1000)
            i++
        }
    }
}