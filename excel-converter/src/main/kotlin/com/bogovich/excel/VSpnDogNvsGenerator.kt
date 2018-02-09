package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import mu.KLogging
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.stream.XMLOutputFactory

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}


class Reader {

    companion object : KLogging()

    fun read() {
        val inputStream = Files.newInputStream(Paths.get("C:/Users/aleksandr.bogovich/Desktop/my staff/practice/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))

        val workSheetHandler = ExcelWorkSheetHandler()
        val cursor = Cursor()
        val out = System.out
        val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))
        val converter = Converter(writer)
        converter.setUp(workSheetHandler)
        val reader = ExcelReader(inputStream, workSheetHandler, cursor)


        writer.document {
            "ЭДПФР" tag {
                "РНПФ" tag {
                    meta(converter, 0, 1, 16, 1) { cursor ->
                        "Реквизиты" tag {
                            "Дата" tag cursor.metaData["B3#1"].orEmpty()
                            "Номер" tag cursor.metaData["D3#1"].orEmpty()
                        }
                        "НПФ" tag {
                            "НаименованиеФормализованное" tag cursor.metaData["D11#1"].orEmpty()
                            "ИНН" tag cursor.metaData["D9#1"].orEmpty()
                        }
                    }
                    stream(converter,
                            { cursor -> cursor.rowNumber >= 16 && cursor.sheetNumber == 1 },
                            { cursor -> cursor.streamData["A"].isNullOrBlank() },
                            { writeStartElement("СписокСведений") },
                            { writeEndElement() }
                    ) { cursor ->
                        "нум" tag 1
                    }
                    reader.read()
                }
            }
        }
        logger.info { "end - $cursor" }
    }

}


fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
    converter.meta(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
            metaCallback))
}

fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, metaCallback: WriterWithCursor) {
    converter.meta(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, metaCallback, Checkpoint.Type.POST))
}

fun DslXMLStreamWriter.meta(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
    converter.meta(Checkpoint(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, metaCallback, Checkpoint.Type.POST))
}


fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, prepareCallback: () -> Unit = {}, endCallback: () -> Unit = {}, streamCallback:
WriterWithCursor) {
    converter.stream(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
            streamCallback,
            prepareCallback = prepareCallback,
            endCallback = endCallback))
}

fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, streamCallback: WriterWithCursor) {
    converter.stream(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, streamCallback,
            Checkpoint.Type.POST))
}

fun DslXMLStreamWriter.stream(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, streamCallback: WriterWithCursor) {
    converter.stream(Checkpoint(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, streamCallback,
            Checkpoint.Type.POST))
}

fun DslXMLStreamWriter.stream(converter: Converter, startCheck: CheckStatement, endCheck: CheckStatement,
                              prepareCallback: () -> Unit = {}, endCallback: () -> Unit = {}, streamCallback: WriterWithCursor) {
    converter.stream(Checkpoint(startCheck, endCheck, streamCallback, Checkpoint.Type.POST, prepareCallback = prepareCallback, endCallback =  endCallback))
}