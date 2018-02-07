package com.bogovich.utils

import org.apache.poi.openxml4j.opc.OPCPackage
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object OPCPackageUtils {
    fun getOPCPackage(filePath: String): OPCPackage = getOPCPackage(File(filePath))
    fun getOPCPackage(file: File): OPCPackage = OPCPackage.open(FileInputStream(file))
    fun getOPCPackage(inputStream: InputStream): OPCPackage = OPCPackage.open(inputStream)
}