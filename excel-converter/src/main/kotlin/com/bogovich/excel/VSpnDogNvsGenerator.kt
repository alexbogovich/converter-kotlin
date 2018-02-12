package com.bogovich.excel

import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KLogging
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.stream.XMLOutputFactory
import kotlin.coroutines.experimental.CoroutineContext

fun main(args: Array<String>) {
    val reader = Reader()
    reader.read()
}

data class Sum(var sum: BigDecimal = BigDecimal.ZERO, var id: BigDecimal = BigDecimal.ZERO)

class Reader {

    companion object : KLogging()

    fun fizz(context: CoroutineContext) = produce<String>(context) {
        while (true) { // sends "Fizz" every 300 ms
            delay(50)
            send("Fizz")
        }
    }

    fun read() {
        val inputStream = Files.newInputStream(Paths.get("C:/Users/aleksandr.bogovich/Desktop/my staff/practice/converter-kotlin/excel-converter/src/test/resources/РНПФ-01.xlsx"))


        fun abc(workSheetHandler: ExcelWorkSheetHandler) = runBlocking<Unit> {
            val workSheetHandler = ExcelWorkSheetHandler()
            val cursor = Cursor()
            val out = System.out
            val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))
            val converter = Converter(writer)
            val reader = ExcelReader(inputStream, workSheetHandler, cursor)
            val channel = Channel<RowData>()
//            workSheetHandler.rowEndCallback = fizz(coroutineContext)
            launch {
                //             this might be heavy CPU-consuming computation or async logic, we'll just send five squares
                reader.read()
//            channel.send(RowData(1, 2, mutableListOf()))
            }
            // here we print five received integers:
            var i = 0
            val mutableMapOf = mutableMapOf<Int, Int>()
            repeat(100) {
//                if (i % 2 == 0) {
//                    println("do some staff")
//                }
//                val list = channel.receive()
//                mutableMapOf.putAll(list)
//                println(mutableMapOf)
//                //delay(1000)
//                i++
            }
        }





//        val total = object {
//            var zlCount: Long = 0
//            val transferSums = object {
//                val sv = Sum()
//                val dsv = Sum()
//                val sofn = Sum()
//                val msk = Sum()
//                var total = BigDecimal.ZERO
//            }
//            var garanty = BigDecimal.ZERO
//            var compensation = BigDecimal.ZERO
//            var totalTransferid = BigDecimal.ZERO
//        }

//        writer.document {
//            "ЭДПФР" tag {
//                "РНПФ" tag {
//                    meta(converter, 0, 1, 16, 1) { cursor ->
//                        "Реквизиты" tag {
//                            "Дата" tag cursor.metaData["B3#1"].orEmpty()
//                            "Номер" tag cursor.metaData["D3#1"].orEmpty()
//                        }
//                        "НПФ" tag {
//                            "НаименованиеФормализованное" tag cursor.metaData["D11#1"].orEmpty()
//                            "ИНН" tag cursor.metaData["D9#1"].orEmpty()
//                        }
//                    }
//                    var count: Long = 0
//                    stream(converter,
//                            { cursor -> cursor.rowNumber >= 16 && cursor.sheetNumber == 1 },
//                            { cursor -> cursor.streamData["A"].isNullOrBlank() },
//                            { writeStartElement("СписокСведений") },
//                            { writeEndElement() }
//                    ) { cursor ->
//                        "Запись" tag {
//                            "НомерПП" tag ++count
//                            "ЗЛ" tag {
//                                "ФИО" tag {
//                                    "Фамилия" tag cursor.cell("B")
//                                    "Имя" tag cursor.cell("C")
//                                    "Отчество" tag cursor.cell("D")
//                                }
//                                "Пол" tag cursor.cell("G")
//                                "ДатаРождения" tag cursor.cell("E")
//                                "МестоРождения" tag {
//                                    "ТипМестаРождения" tag "СТАНДАРТНОЕ"
//                                    "ГородРождения" tag cursor.cell("F")
//                                    "СтранаРождения" tag "РФ"
//                                }
//                                "СтраховойНомер" tag "${cursor.cell("H")} ${cursor.cell("I")}"
//                            }
//                            "СуммыПереданные" tag {
//                                "СВ" tag {
//                                    "Сумма" tag cursor.cell("J")
//                                    "ИД" tag cursor.cell("K")
//                                }
//                                "ДСВ" tag {
//                                    "Сумма" tag cursor.cell("L")
//                                    "ИД" tag cursor.cell("M")
//                                }
//                                "СОФН" tag {
//                                    "Сумма" tag cursor.cell("N")
//                                    "ИД" tag cursor.cell("O")
//                                }
//                                "МСК" tag {
//                                    "Сумма" tag cursor.cell("P")
//                                    "ИД" tag cursor.cell("Q")
//                                }
//                                "ВсегоСПН" tag cursor.cell("R")
//                            }
//                            "ГарантийноеВосполнение" tag cursor.cell("S")
//                            "Компенсация" tag cursor.cell("T")
//                            "ВсегоПередано" tag cursor.cell("U")
//                        }
//                    }
//                    reader.read()
//                }
//            }
//        }
//        logger.info { "end - $cursor" }
    }

}


//fun BigDecimal.addTo(sumTo: BigDecimal): BigDecimal {
//    sumTo.add(this)
//    return this
//}
//
//fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
//    converter.meta(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
//            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
//            metaCallback))
//}
//
//fun DslXMLStreamWriter.meta(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, metaCallback: WriterWithCursor) {
//    converter.meta(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, metaCallback, Checkpoint.Type.POST))
//}
//
//fun DslXMLStreamWriter.meta(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, metaCallback: WriterWithCursor) {
//    converter.meta(Checkpoint(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, metaCallback, Checkpoint.Type.POST))
//}
//
//
//fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endRow: Int, endSheet: Int = 1, prepareCallback: () -> Unit = {}, endCallback: () -> Unit = {}, streamCallback:
//WriterWithCursor) {
//    converter.stream(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) },
//            { cursor -> cursor.isCheckpoint(endRow, endSheet) },
//            streamCallback,
//            prepareCallback = prepareCallback,
//            endCallback = endCallback))
//}
//
//fun DslXMLStreamWriter.stream(converter: Converter, startRow: Int = 0, startSheet: Int = 1, endCheck: CheckStatement, streamCallback: WriterWithCursor) {
//    converter.stream(Checkpoint({ cursor -> cursor.isCheckpoint(startRow, startSheet) }, endCheck, streamCallback,
//            Checkpoint.Type.POST))
//}
//
//fun DslXMLStreamWriter.stream(converter: Converter, startCheck: CheckStatement, endRow: Int, endSheet: Int = 1, streamCallback: WriterWithCursor) {
//    converter.stream(Checkpoint(startCheck, { cursor -> cursor.isCheckpoint(endRow, endSheet) }, streamCallback,
//            Checkpoint.Type.POST))
//}
//
//fun DslXMLStreamWriter.stream(converter: Converter, startCheck: CheckStatement, endCheck: CheckStatement,
//                              prepareCallback: () -> Unit = {}, endCallback: () -> Unit = {}, streamCallback: WriterWithCursor) {
//    converter.stream(Checkpoint(startCheck, endCheck, streamCallback, Checkpoint.Type.POST, prepareCallback = prepareCallback, endCallback = endCallback))
//}