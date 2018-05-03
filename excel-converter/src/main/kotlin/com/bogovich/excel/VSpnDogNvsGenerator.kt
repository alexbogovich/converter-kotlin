package com.bogovich.excel

import com.bogovich.utils.AfValidationUtils
import com.bogovich.utils.InsuredPersonUtils
import com.bogovich.xml.writer.dsl.CoroutineXMLStreamWriter
import kotlinx.coroutines.experimental.cancelAndJoin
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import mu.KotlinLogging
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.math.BigDecimal
import java.util.zip.GZIPOutputStream
import javax.xml.stream.XMLOutputFactory


fun main(args: Array<String>) = runBlocking {
    val logger = KotlinLogging.logger {}
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

    val file = File("h:\\ПФР_777000_РНПФ_${converter.commonValue.localDateTime.toLocalDate()}_${converter.commonValue.guid}.xml.gz")
    val fileWriter = BufferedWriter(OutputStreamWriter(GZIPOutputStream(FileOutputStream(file)), "UTF-8"))
//
//    val out = System.out
//    converter.writer = CoroutineXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out, "UTF-8"))

    converter.writer = CoroutineXMLStreamWriter(XMLOutputFactory.newInstance().createXMLStreamWriter(fileWriter))

    try {
        map(converter)
    } catch (e: Exception) {
        logger.error { "Exception on converting: ${e.message}" }
    } finally {
        job.cancelAndJoin()
        fileWriter.close()
        logger.info { "Successful create ${file.absoluteFile}" }
    }

    val errors = AfValidationUtils.getNewErrorList()
    AfValidationUtils.validateDocument(file, "РНПФ", "C:/Users/aleksandr.bogovich/Desktop/uspn/Design&Analysis/Technical Specification/Альбом Форматов/АФ 2.19.2д 17.01.2018/Схемы", errors)
}

suspend fun map(converter: Converter) {
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

    converter.document {
        "ЭДПФР" {
            defaultNamespace("http://пф.рф/ВсВО/НПФ/РНПФ/2017-08-09")
            namespace("ВНПФ", "http://пф.рф/ВсВО/НПФ/типыВходящие/2017-08-09")
            namespace("НПФ", "http://пф.рф/ВсВО/НПФ/типы/2017-08-09")
            namespace("УТ", "http://пф.рф/унифицированныеТипы/2014-01-01")
            namespace("АФ", "http://пф.рф/АФ")
            "РНПФ" {
                "Реквизиты" {
                    "УТ:Дата" tag converter.cellDate("B3")
                    "УТ:Номер" tag converter.cell("D3")
                }
                "НПФ" {
                    "НПФ:НаименованиеФормализованное" tag converter.cell("D11")
                    "НПФ:ИНН" tag converter.cell("D9")
                }
                "СписокСведений" {
                    converter.stream({ row -> row.rowNum >= 16 && row.sheetNum == 1 },
                            { row -> row.data["A"]?.stringCellValue.isNullOrEmpty() }) {
                        var rowTotalSpn = BigDecimal.ZERO
                        "Запись" {
                            "ВНПФ:НомерПП" tag ++total.zlCount
                            "ВНПФ:ЗЛ" {
                                "УТ:ФИО" {
                                    "УТ:Фамилия" tag cell("B")
                                    "УТ:Имя" tag cell("C")
                                    "УТ:Отчество" tag cell("D")
                                }
                                "УТ:Пол" tag cell("G")
                                "УТ:ДатаРождения" tag cellDate("E")
                                "УТ:МестоРождения" {
                                    "УТ:ТипМестаРождения" tag "СТАНДАРТНОЕ"
                                    "УТ:ГородРождения" tag cell("F")
                                    "УТ:СтранаРождения" tag "РФ"
                                }
                                "УТ:СтраховойНомер" tag InsuredPersonUtils.formattedNumber(cellLong("H"))
                            }
                            "НПФ:СуммыПереданные" {
                                "НПФ:СВ" {
                                    "НПФ:Сумма" tag cellMoney("J").also {
                                        total.transferSums.sv.sum += it
                                        rowTotalSpn += it
                                    }
                                    "НПФ:ИД" tag cellMoney("K").also {
                                        total.transferSums.sv.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "НПФ:ДСВ" {
                                    "НПФ:Сумма" tag cellMoney("L").also {
                                        total.transferSums.dsv.sum += it
                                        rowTotalSpn += it
                                    }
                                    "НПФ:ИД" tag cellMoney("M").also {
                                        total.transferSums.dsv.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "НПФ:СОФН" {
                                    "НПФ:Сумма" tag cellMoney("N").also {
                                        total.transferSums.sofn.sum += it
                                        rowTotalSpn += it
                                    }
                                    "НПФ:ИД" tag cellMoney("O").also {
                                        total.transferSums.sofn.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "НПФ:МСК" {
                                    "НПФ:Сумма" tag cellMoney("P").also {
                                        total.transferSums.msk.sum += it
                                        rowTotalSpn += it
                                    }
                                    "НПФ:ИД" tag cellMoney("Q").also {
                                        total.transferSums.msk.id += it
                                        rowTotalSpn += it
                                    }
                                }
                                "НПФ:ВсегоСПН" tag rowTotalSpn.also { total.transferSums.total += it }
                            }
                            "НПФ:ГарантийноеВосполнение" tag cellMoney("S").also {
                                total.garanty += it
                                rowTotalSpn += it
                            }
                            "НПФ:Компенсация" tag cellMoney("T").also {
                                total.compensation += it
                                rowTotalSpn += it
                            }
                            "НПФ:ВсегоПередано" tag rowTotalSpn.also { total.totalTransferred += it }
                        }
                    }
                }
                "Итого" {
                    "КоличествоЗЛ" tag total.zlCount
                    "НПФ:СуммыПереданные" tag {
                        "НПФ:СВ" {
                            "НПФ:Сумма" tag total.transferSums.sv.sum
                            "НПФ:ИД" tag total.transferSums.sv.id
                        }
                        "НПФ:ДСВ" {
                            "НПФ:Сумма" tag total.transferSums.dsv.sum
                            "НПФ:ИД" tag total.transferSums.dsv.id
                        }
                        "НПФ:СОФН" {
                            "НПФ:Сумма" tag total.transferSums.sofn.sum
                            "НПФ:ИД" tag total.transferSums.sofn.id
                        }
                        "НПФ:МСК" {
                            "НПФ:Сумма" tag total.transferSums.msk.sum
                            "НПФ:ИД" tag total.transferSums.msk.id
                        }
                        "НПФ:ВсегоСПН" tag total.transferSums.total
                    }
                    "НПФ:ГарантийноеВосполнение" tag total.garanty
                    "НПФ:Компенсация" tag total.compensation
                    "НПФ:ВсегоПередано" tag total.totalTransferred
                }
            }
            "СлужебнаяИнформация" {
                "АФ:GUID" tag converter.commonValue.guid
                "АФ:ДатаВремя" tag converter.commonValue.localDateTime
                "НПФ:Составитель" tag ""
                "НПФ:НомерДокументаОрганизации" tag 123
                "НПФ:ЗаГод" tag converter.commonValue.localDateTime.year
            }
        }
    }
}