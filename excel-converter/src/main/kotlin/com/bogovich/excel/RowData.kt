package com.bogovich.excel

import org.apache.poi.ss.usermodel.Cell
import java.time.LocalDate
import java.time.ZoneId

data class RowData (val sheetNum: Int, val rowNum: Int, val data:Map<String, Cell>) {
    fun cell(shortRef: String, removeDoubleQuotes: Boolean = true): String {
        val s = data[shortRef]?.stringCellValue.orEmpty()
        if (removeDoubleQuotes && s.startsWith("\"") && s.endsWith("\"")) {
            return s.removeSurrounding("\"")
        }
        return s
    }
    fun cellDate(shortRef: String): LocalDate {
        return data[shortRef]?.dateCellValue!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}



