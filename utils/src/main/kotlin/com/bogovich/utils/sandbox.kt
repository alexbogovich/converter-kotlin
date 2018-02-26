package com.bogovich.utils

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl
import org.w3c.dom.ls.LSResourceResolver
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors.groupingBy
import java.util.zip.ZipInputStream
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory


fun main(args: Array<String>) {
    val schemas = getAllSchemas("/home/alex/Documents/АФ 2.20.6д/Схемы")


    val validator = getSchemaFactory().newSchema(schemas["РНПФ-А"]!!.first().toFile()).newValidator()

    val errors = mutableListOf<SAXParseException?>()

    validator.errorHandler = object : ErrorHandler {
        override fun warning(exception: SAXParseException?) {
            errors.add(exception)
        }

        override fun error(exception: SAXParseException?) {
            errors.add(exception)
        }

        override fun fatalError(exception: SAXParseException?) {
            errors.add(exception)
        }
    }

    val targetFile = File("/home/alex/Documents/АФ 2.20" +
            ".6д/Примеры/ВСВО/НПФ/Входящие/ПФР_7707492166_000_РНПФ-А_20170809_c23e739a-090f-49d8-a10a-c8ea16535f3f.XML.zip")
    val validateStream = StreamSource(ZipInputStream(FileInputStream(targetFile)))



    validator.validate(validateStream)

    println(errors)
}


fun getAllSchemas(schemaFolder: String): Map<String, List<Path>> {
    val containDateRegex = ".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex()
    Files.walk(Paths.get(schemaFolder)).use({ paths ->
        return paths
                .filter { path -> path.toFile().isFile }
                .filter { path -> path.isXsdAndNameContain(containDateRegex) }
                .peek({ t -> println(t) })
                .collect(groupingBy { path: Path -> path.fileName.toString().split("_").first() })
    })
}

fun Path.isXsdAndNameContain(pattern: Regex) = fileName.toString().run {
    endsWith(".xsd") && contains(pattern)
}

fun getSchemaFactory(): SchemaFactory {
    return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).run {
        resourceResolver = LSResourceResolver { type, namespaceURI, publicId, systemId, baseURI ->
            println("Invoke method 'resolveResource': $type, $namespaceURI, $publicId, $systemId, $baseURI")
            val parentFolder = File(URI(baseURI)).parentFile
            val xsdFile = File(parentFolder, systemId)
            val inputStream = FileInputStream(xsdFile)
            val childSystemId = xsdFile.toURI().toString()
            DOMInputImpl(publicId, childSystemId, systemId, inputStream, "UTF-8")
        }
        this
    }
}
