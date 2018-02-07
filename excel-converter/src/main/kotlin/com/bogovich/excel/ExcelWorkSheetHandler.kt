package com.bogovich.excel

import mu.KLogging
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment

class ExcelWorkSheetHandler: XSSFSheetXMLHandler.SheetContentsHandler {

    companion object: KLogging()

    lateinit var rowEndCallback: () -> Unit
    lateinit var rowStartCallback: () -> Unit
    lateinit var cellCallback: (cellReference: String, formattedValue: String) -> Unit

    override fun endRow(rowNum: Int) {
        logger.info { "endRow rowNum = $rowNum" }
        if (this::rowEndCallback.isInitialized) {
            rowEndCallback.invoke()
        }
    }

    override fun headerFooter(text: String?, isHeader: Boolean, tagName: String?) {
        logger.info { "headerFooter text = $text isHeader = $isHeader tagName = $tagName" }
    }

    override fun startRow(rowNum: Int) {
        logger.info { "startRow rowNum = $rowNum" }
        if (this::rowStartCallback.isInitialized) {
            rowStartCallback.invoke()
        }
    }

    override fun cell(cellReference: String?, formattedValue: String?, comment: XSSFComment?) {
        logger.info { "cell cellReference = $cellReference formattedValue = $formattedValue comment = $comment" }
        if (this::cellCallback.isInitialized) {
            cellCallback.invoke(cellReference!!, formattedValue!!)
        }
    }
}