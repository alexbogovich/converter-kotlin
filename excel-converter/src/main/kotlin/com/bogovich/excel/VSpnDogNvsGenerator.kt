package com.bogovich.excel

import com.bogovich.utils.money
import com.bogovich.xml.writer.dsl.DslXMLStreamWriter
import com.monitorjbl.xlsx.StreamingReader
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import org.apache.poi.ss.usermodel.Cell
import java.nio.file.Files
import java.nio.file.Paths
import javax.xml.stream.XMLOutputFactory

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
    val writer = DslXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))


    writer.document {
        "ЭДПФР" tag {
            "Реквизиты" tag {
                "Дата" tag converter.cell("B3")
                "Номер" tag converter.cell("D3")
            }
            "НПФ" tag {
                "НаименованиеФормализованное" tag converter.cell("D11")
                "ИНН" tag converter.cell("D9")
            }
            "СписокСведений" tag {
                var count: Long = 0
                converter.stream({ c -> c.rowNum >= 16 && c.sheetNum == 1 },
                        { c -> c.getRow().data["A"]?.data.isNullOrEmpty()
                        }) { row ->
                    "Запись" tag {
                        "НомерПП" tag ++count
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
                                "Сумма" tag row.cell("J").money()
                                "ИД" tag row.cell("K").money()
                            }
                            "ДСВ" tag {
                                "Сумма" tag row.cell("L").money()
                                "ИД" tag row.cell("M").money()
                            }
                            "СОФН" tag {
                                "Сумма" tag row.cell("N").money()
                                "ИД" tag row.cell("O").money()
                            }
                            "МСК" tag {
                                "Сумма" tag row.cell("P").money()
                                "ИД" tag row.cell("Q").money()
                            }
                            "ВсегоСПН" tag row.cell("R").money()
                        }
                        "ГарантийноеВосполнение" tag row.cell("S").money()
                        "Компенсация" tag row.cell("T").money()
                        "ВсегоПередано" tag row.cell("U").money()
                    }
                }
            }
        }
    }

    job.cancelAndJoin()
    println("Done!")
}