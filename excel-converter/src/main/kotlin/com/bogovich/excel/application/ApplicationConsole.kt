package com.bogovich.excel.application

import kotlinx.cli.CommandLineInterface
import kotlinx.cli.flagValueArgument
import kotlinx.cli.parse
import kotlinx.cli.positionalArgumentsList
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val cli = CommandLineInterface("Converter")
    val documentFolder by cli.flagValueArgument(listOf("-s, --sourceFolder"), "sourceDir", "Source Dir", "")
    val docList by cli.positionalArgumentsList("-d", "Document List")
    val schemaFolder by cli.flagValueArgument("-v", "afSchemaFolder", "Af Schema Folder", "")

    try {
        cli.parse(args)
    }
    catch (e: Exception) {
        exitProcess(1)
    }

    println("Args: ${args.asList()}")
    println("$documentFolder $docList $schemaFolder")
}