package com.bogovich.excel

import com.bogovich.utils.money
import com.bogovich.utils.toLocalDate
import com.bogovich.xml.writer.dsl.CoroutineXMLStreamWriter
import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.apache.poi.ss.usermodel.Cell
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*
import javax.xml.stream.XMLOutputFactory


data class Sum(var sum: BigDecimal = BigDecimal.ZERO, var id: BigDecimal = BigDecimal.ZERO)


fun main(args: Array<String>) = runBlocking {
    val channel = Channel<RowData>()
    val job = launch(coroutineContext) {
        val inputStream = Files.newInputStream(Paths.get("C:/Users/aleksandr.bogovich/Desktop/my staff/practice/converter-kotlin/excel-converter/src/main/resources/РНПФ-01.xlsx"))
        inputStream.use { stream ->
            val workbook = StreamingReader.builder()
                    .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
                    .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
                    .open(stream)            // InputStream or File for XLSX file (required)
            for ((sheetNum, sheet) in workbook.withIndex()) {
//                println(sheet.sheetName)
                for (row in sheet) {
//                    println("read row ${row.rowNum}")
                    row.asSequence()
                            .filter { cell: Cell -> !cell.stringCellValue.isNullOrEmpty() }
                            .map { cell: Cell -> CellData.of(cell) }
                            .associateBy { cellData: CellData -> cellData.ref }
                            .also { cells ->
                                //println("prepare to send row ${row.rowNum}")
                                channel.send(RowData(sheetNum + 1, row.rowNum, cells))
                                //println("sent to send row ${row.rowNum}")
                            }
                }
            }
        }
    }

    val converter = Converter(channel)
    val out = System.out
    val writer = CoroutineXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))

    val total = object {
        var zlCount: Long = 0
        val transferSums = object {
            val sv = Sum()
            val dsv = Sum()
            val sofn = Sum()
            val msk = Sum()
            var total = BigDecimal.ZERO
        }
        var garanty = BigDecimal.ZERO
        var compensation = BigDecimal.ZERO
        var totalTransferid = BigDecimal.ZERO
    }

    writer.document {
        "ЭДПФР" tag {
            "Реквизиты" tag {
                "Дата" tag converter.cell("B3").toLocalDate()
                "Номер" tag converter.cell("D3")
            }
            "НПФ" tag {
                "НаименованиеФормализованное" tag converter.cell("D11")
                "ИНН" tag converter.cell("D9")
            }
            "СписокСведений" tag {
                converter.stream({ c -> c.rowNum >= 16 && c.sheetNum == 1 },
                        { c -> c.getRow().data["A"]?.data.isNullOrEmpty() }) { row ->
                    var rowTotalSpn = BigDecimal.ZERO
                    "Запись" tag {
                        "НомерПП" tag ++total.zlCount
                        "ЗЛ" tag {
                            "ФИО" tag {
                                "Фамилия" tag row.cell("B")
                                "Имя" tag row.cell("C")
                                "Отчество" tag row.cell("D")
                            }
                            "Пол" tag row.cell("G")
                            "ДатаРождения" tag row.cell("E")
                            "МестоРождения" tag {
                                "ТипМестаРождения" tag "СТАНДАРТНОЕ"
                                "ГородРождения" tag row.cell("F")
                                "СтранаРождения" tag "РФ"
                            }
                            "СтраховойНомер" tag "${row.cell("H")} ${row.cell("I")}"
                        }
                        "СуммыПереданные" tag {
                            "СВ" tag {
                                "Сумма" tag row.cell("J").money().also {
                                    total.transferSums.sv.sum += it
                                    rowTotalSpn += it
                                }
                                "ИД" tag row.cell("K").money().also {
                                    total.transferSums.sv.id += it
                                    rowTotalSpn += it
                                }
                            }
                            "ДСВ" tag {
                                "Сумма" tag row.cell("L").money().also {
                                    total.transferSums.dsv.sum += it
                                    rowTotalSpn += it
                                }
                                "ИД" tag row.cell("M").money().also {
                                    total.transferSums.dsv.id += it
                                    rowTotalSpn += it
                                }
                            }
                            "СОФН" tag {
                                "Сумма" tag row.cell("N").money().also {
                                    total.transferSums.sofn.sum += it
                                    rowTotalSpn += it
                                }
                                "ИД" tag row.cell("O").money().also {
                                    total.transferSums.sofn.id += it
                                    rowTotalSpn += it
                                }
                            }
                            "МСК" tag {
                                "Сумма" tag row.cell("P").money().also {
                                    total.transferSums.msk.sum += it
                                    rowTotalSpn += it
                                }
                                "ИД" tag row.cell("Q").money().also {
                                    total.transferSums.msk.id += it
                                    rowTotalSpn += it
                                }
                            }
                        }
                        "ВсегоСПН" tag rowTotalSpn.also { total.transferSums.total += it }
                    }
                    "ГарантийноеВосполнение" tag row.cell("S").money().also {
                        total.garanty += it
                        rowTotalSpn += it
                    }
                    "Компенсация" tag row.cell("T").money().also {
                        total.compensation += it
                        rowTotalSpn += it
                    }
                    "ВсегоПередано" tag rowTotalSpn.also { total.totalTransferid += it }
                }
            }
            "Итого" tag {
                "КоличествоЗЛ" tag total.zlCount
                "СуммыПереданные" tag {
                    "СуммыПереданные" tag {
                        "СВ" tag {
                            "Сумма" tag total.transferSums.sv.sum
                            "ИД" tag total.transferSums.sv.id
                        }
                        "ДСВ" tag {
                            "Сумма" tag total.transferSums.dsv.sum
                            "ИД" tag total.transferSums.dsv.id
                        }
                        "СОФН" tag {
                            "Сумма" tag total.transferSums.sofn.sum
                            "ИД" tag total.transferSums.sofn.id
                        }
                        "МСК" tag {
                            "Сумма" tag total.transferSums.msk.sum
                            "ИД" tag total.transferSums.msk.id
                        }
                        "ВсегоСПН" tag total.transferSums.total
                    }
                }
                "ГарантийноеВосполнение" tag total.garanty
                "Компенсация" tag total.compensation
                "ВсегоПередано" tag total.totalTransferid
            }
            "СлужебнаяИнформация" tag {
                "GUID" tag UUID.randomUUID()
                "ДатаВремя" tag LocalDateTime.now()
                "ЗаГод" tag LocalDateTime.now().year
            }
        }
    }

    job.cancelAndJoin()
//    println("Done!")
}