package com.bogovich.excel

import mu.KLogging
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment

class ExcelWorkSheetHandler : XSSFSheetXMLHandler.SheetContentsHandler {

    companion object : KLogging()

    val cellData = mutableListOf<CellData>()
    lateinit var sheetData: SheetData
    lateinit var rowEndCallback: RowDataCallback

    //    lateinit var cursor: Cursor
//    lateinit var rowStartCallback: CursorOperation
//    lateinit var cellCallback: CursorOperation

    override fun endRow(rowNum: Int) {
        logger.info { "endRow rowNum = $rowNum" }
        if (this::rowEndCallback.isInitialized) {
            //rowEndCallback(RowData(sheetData.sheetNum, rowNum, cellData.toList()))
        }
        cellData.clear()
    }

    override fun headerFooter(text: String?, isHeader: Boolean, tagName: String?) {
        logger.info { "headerFooter text = $text isHeader = $isHeader tagName = $tagName" }
    }

    override fun startRow(rowNum: Int) {
        logger.info { "startRow rowNum = $rowNum" }
    }

    override fun cell(cellReference: String?, formattedValue: String?, comment: XSSFComment?) {
        logger.info { "cell cellReference = $cellReference formattedValue = $formattedValue" }
        if (!cellReference.isNullOrBlank() && !formattedValue.isNullOrBlank()) {
            cellData.add(CellData(cellReference!!, formattedValue!!))
        }
    }
}