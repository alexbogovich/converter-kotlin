package com.bogovich.excel

import com.bogovich.utils.money
import org.apache.poi.ss.usermodel.Cell
import java.math.BigDecimal
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
    fun cellMoney(shortRef: String, removeDoubleQuotes: Boolean = true): BigDecimal {
        return cell(shortRef, removeDoubleQuotes).money()
    }
    fun cellDate(shortRef: String): LocalDate {
        return data[shortRef]?.dateCellValue!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
}



