package com.bogovich.excel

import com.bogovich.utils.money
import com.bogovich.xml.writer.dsl.CoroutineXMLStreamWriter
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.xml.stream.XMLOutputFactory


data class Sum(var sum: BigDecimal = BigDecimal.ZERO, var id: BigDecimal = BigDecimal.ZERO)

fun main(args: Array<String>) = runBlocking {
    val converter = Converter()

    val job = launch {
        args.forEachIndexed { index, s ->
            when (index) {
                0 -> {
                    converter.reader.readMainDoc(s)
                }
                else -> {
                    converter.reader.readRestDocs(s, index + 1, args.size)
                }
            }
        }
        converter.closeChannels()
    }

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
        var totalTransferred = BigDecimal.ZERO
    }

    writer.document {
        "ЭДПФР" tag {
            "Реквизиты" tag {
                "Дата" tag converter.cellDate("B3")
                "Номер" tag converter.cell("D3")
            }
            "НПФ" tag {
                "НаименованиеФормализованное" tag converter.cell("D11")
                "ИНН" tag converter.cell("D9")
            }
            "СписокСведений" tag {
                converter.stream({ row -> row.rowNum >= 16 && row.sheetNum == 1 },
                        { row -> row.data["A"]?.stringCellValue.isNullOrEmpty() }) { row ->
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
                            "ДатаРождения" tag row.cellDate("E")
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
                    "ВсегоПередано" tag rowTotalSpn.also { total.totalTransferred += it }
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
                "ВсегоПередано" tag total.totalTransferred
            }
            "СлужебнаяИнформация" tag {
                "GUID" tag UUID.randomUUID()
                "ДатаВремя" tag LocalDateTime.now()
                "ЗаГод" tag LocalDateTime.now().year
            }
        }
    }
    job.cancelAndJoin()
}