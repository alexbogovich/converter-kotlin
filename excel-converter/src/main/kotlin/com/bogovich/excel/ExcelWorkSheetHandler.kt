package com.bogovich.excel

import mu.KLogging
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment

class ExcelWorkSheetHandler : XSSFSheetXMLHandler.SheetContentsHandler {

    companion object : KLogging()

    lateinit var cursor: Cursor
    lateinit var rowEndCallback: CursorOperation
    lateinit var rowStartCallback: CursorOperation
    lateinit var cellCallback: CursorOperation

    override fun endRow(rowNum: Int) {
        logger.info { "endRow rowNum = $rowNum" }
        if (this::rowEndCallback.isInitialized) {
            rowEndCallback(cursor)
        }
        if(cursor.mode == Cursor.ReadMode.META) {
            cursor.saveStreamToMeta()
        }
        cursor.streamData.clear()
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
            rowStartCallback(cursor)
        }
    }

    override fun cell(cellReference: String?, formattedValue: String?, comment: XSSFComment?) {
        logger.info { "cell cellReference = $cellReference formattedValue = $formattedValue" }

        cursor.apply {
            reference = cellReference!!
            value = formattedValue!!
            val refPrefix = reference.replace("[0-9]+".toRegex(), "")
            streamData[refPrefix] = value
        }
        if (this::cellCallback.isInitialized) {
            cellCallback(cursor)
        }

    }
}