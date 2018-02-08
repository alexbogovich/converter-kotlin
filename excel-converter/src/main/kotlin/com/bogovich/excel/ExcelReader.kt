package com.bogovich.excel

import mu.KLogging
import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.openxml4j.opc.PackageAccess
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.model.StylesTable
import org.xml.sax.InputSource
import java.io.File
import java.io.InputStream
import javax.xml.parsers.SAXParserFactory

class ExcelReader(val xlsxPackage: OPCPackage, val sheetContentsHandler: XSSFSheetXMLHandler.SheetContentsHandler) {

    companion object : KLogging()

    constructor(filePath: String, sheetContentsHandler: XSSFSheetXMLHandler.SheetContentsHandler) : this(OPCPackage.open(filePath, PackageAccess.READ), sheetContentsHandler)
    constructor(inputStream: InputStream, sheetContentsHandler: XSSFSheetXMLHandler.SheetContentsHandler) : this(OPCPackage.open(inputStream), sheetContentsHandler)
    constructor(file: File, sheetContentsHandler: XSSFSheetXMLHandler.SheetContentsHandler) : this(OPCPackage.open(file), sheetContentsHandler)

    fun read() {
        val strings = ReadOnlySharedStringsTable(this.xlsxPackage)
        val xssfReader = XSSFReader(this.xlsxPackage)
        val worksheets = xssfReader.sheetsData as XSSFReader.SheetIterator

        worksheets.forEach { inputStream ->
            logger.info { "start new sheet '${worksheets.sheetName}'" }
            inputStream.use {
                readSheet(xssfReader.stylesTable, strings, inputStream)
            }
            logger.info { "finish sheet '${worksheets.sheetName}'" }
        }
    }

    private fun readSheet(stylesTable: StylesTable, strings: ReadOnlySharedStringsTable, inputStream: InputStream) {
        val sheetsParser = SAXParserFactory.newInstance().newSAXParser().xmlReader
        sheetsParser.contentHandler = XSSFSheetXMLHandler(stylesTable, strings, sheetContentsHandler, false)
        sheetsParser.parse(InputSource(inputStream))
    }
}