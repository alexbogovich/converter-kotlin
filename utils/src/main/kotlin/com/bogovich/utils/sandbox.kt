package com.bogovich.utils

import com.bogovich.utils.AfValidationUtils.validateDocument
import org.xml.sax.SAXParseException
import java.io.File


fun main(args: Array<String>) {
    val errors = mutableListOf<SAXParseException?>()

    val targetFile = File("C:/Users/aleksandr.bogovich/Desktop/uspn/Design&Analysis/Technical Specification/Альбом Форматов/АФ 2.19.2д 17.01.2018/Примеры/ВСВО/НПФ/Входящие/ПФР_7707492166_000_РНПФ-М_20170809_786f1997-bd1e-4122-ba04-5a114ef6e70a.xml")

    validateDocument(targetFile, "РНПФ-М", "C:/Users/aleksandr.bogovich/Desktop/uspn/Design&Analysis/Technical Specification/Альбом Форматов/АФ 2.19.2д 17.01.2018/Схемы", errors)
    println(errors)
}