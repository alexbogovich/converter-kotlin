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
import java.util.zip.GZIPInputStream
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory


fun main(args: Array<String>) {
    val schemas = getAllSchemas("C:\\Users\\aleksandr.bogovich\\Desktop\\uspn\\Design&Analysis\\Technical Specification\\Альбом Форматов\\АФ 2.19.2д 17.01.2018\\Схемы")


    val validator = getSchemaFactory().newSchema(schemas["УЗР"]!!.first().toFile()).newValidator()

    val errors = mutableListOf<SAXParseException?>()

    validator.errorHandler = object: ErrorHandler {
        override fun warning(exception: SAXParseException?) { errors.add(exception) }
        override fun error(exception: SAXParseException?) { errors.add(exception) }
        override fun fatalError(exception: SAXParseException?) { errors.add(exception) }
    }

    val targetFile = File("H:\\ПФР_777000_УЗР_2018-02-16_d91283f3-039a-4887-9000-35e38ebdbdc3.xml.gz")
    val validateStream = StreamSource(GZIPInputStream(FileInputStream(targetFile)))

    validator.validate(validateStream)

    println(errors)
}


fun getAllSchemas(schemaFolder: String): Map<String, List<Path>> {
    val containDateRegex = ".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex()
    Files.walk(Paths.get(schemaFolder)).use({ paths ->
        return paths
                .filter { path -> path.toFile().isFile }
                .filter { path ->
                    path.fileName.toString().run {
                        endsWith(".xsd") && contains(containDateRegex)
                    }
                }
                .collect(groupingBy { path: Path -> path.fileName.toString().split("_").first() })
    })
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
