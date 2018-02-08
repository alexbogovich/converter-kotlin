package com.bogovich.excel

import mu.KLogging
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment

class ExcelWorkSheetHandler() : XSSFSheetXMLHandler.SheetContentsHandler {

    companion object: KLogging()

    lateinit var cursor: Cursor
    lateinit var rowEndCallback: (cursor: Cursor) -> Unit
    lateinit var rowStartCallback: (cursor: Cursor) -> Unit
    lateinit var cellCallback: (cursor: Cursor) -> Unit

    override fun endRow(rowNum: Int) {
        logger.info { "endRow rowNum = $rowNum" }
        if (this::rowEndCallback.isInitialized) {
            cursor.run(rowEndCallback)
            if (cursor.mode == Cursor.ReadMode.STREAM) {
                cursor.streamData.clear()
            }
        }
    }

    override fun headerFooter(text: String?, isHeader: Boolean, tagName: String?) {
        logger.info { "headerFooter text = $text isHeader = $isHeader tagName = $tagName" }
    }

    override fun startRow(rowNum: Int) {
        logger.info { "startRow rowNum = $rowNum" }

        cursor.apply {
            rowNumber = rowNum
            if (mode == Cursor.ReadMode.STREAM && streamData.isNotEmpty()) {
                streamData.clear()
            }
        }

        if (this::rowStartCallback.isInitialized) {
            cursor.run(rowStartCallback)
        }
    }

    override fun cell(cellReference: String?, formattedValue: String?, comment: XSSFComment?) {
        logger.info { "cell cellReference = $cellReference formattedValue = $formattedValue" }

        cursor.apply {
            reference = cellReference!!
            value = formattedValue!!
            if (mode == Cursor.ReadMode.META) {
                metaData["$reference#$sheetNumber"] = value
            } else if (mode == Cursor.ReadMode.STREAM) {
                val refPrefix = reference.replace("[0-9]", "")
                streamData[refPrefix] = value
            }
        }
        if (this::cellCallback.isInitialized) {
            cursor.run(cellCallback)
        }

    }
}