package com.bogovich.utils

import com.sun.org.apache.xerces.internal.dom.DOMInputImpl
import mu.KotlinLogging
import org.w3c.dom.ls.LSResourceResolver
import org.xml.sax.ErrorHandler
import org.xml.sax.SAXParseException
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.Validator

object AfValidationUtils {
    private val logger = KotlinLogging.logger {}

    fun getSchemaFactory(): SchemaFactory {
        return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).run {
            resourceResolver = LSResourceResolver { type, namespaceURI, publicId, systemId, baseURI ->
                logger.debug { "Invoke method 'resolveResource': $type, $namespaceURI, $publicId, $systemId, $baseURI" }
                val parentFolder = File(URI(baseURI)).parentFile
                val xsdFile = File(parentFolder, systemId)
                val inputStream = FileInputStream(xsdFile)
                val childSystemId = xsdFile.toURI().toString()
                DOMInputImpl(publicId, childSystemId, systemId, inputStream, "UTF-8")
            }
            this
        }
    }

    fun getAllSchemas(schemaFolder: String): Map<String, List<Path>> {
        val containDateRegex = ".*(\\d{4}-\\d{2}-\\d{2}).*".toRegex()
        Files.walk(Paths.get(schemaFolder)).use({ paths ->
            return paths
                    .filter { path -> path.toFile().isFile }
                    .filter { path -> path.isXsdAndNameContain(containDateRegex) }
                    .collect(Collectors.groupingBy { path: Path ->
                        path.fileName.toString().split("_").first()
                    })
                    .also {
                        logger.info { "Map ${it.size} schemas in $schemaFolder" }
                    }
        })
    }

    fun getValidator(afFileType: String, pathToSchemaFolder: String, errorTargetCollection: MutableCollection<SAXParseException?>): Validator {
        val schemas = getAllSchemas(pathToSchemaFolder)
        val validator = getSchemaFactory().newSchema(schemas[afFileType]!!.first().toFile()).newValidator()
        validator.errorHandler = object : ErrorHandler {
            override fun warning(exception: SAXParseException?) {
                errorTargetCollection.add(exception)
                logger.info { "Find validate warning ${exception?.message}" }
            }

            override fun error(exception: SAXParseException?) {
                errorTargetCollection.add(exception)
                logger.info { "Find validate error ${exception?.message}" }
            }

            override fun fatalError(exception: SAXParseException?) {
                errorTargetCollection.add(exception)
                logger.info { "Find validate fatalError ${exception?.message}" }
            }
        }
        return validator
    }

    fun validateDocument(documentFile: File, afFileType: String, pathToSchemaFolder: String, errorTargetCollection:
    MutableCollection<SAXParseException?> = getNewErrorList()): Collection<SAXParseException?> {
        getValidator(afFileType, pathToSchemaFolder, errorTargetCollection).validate(getSource(documentFile))
        if (errorTargetCollection.isEmpty()) {
            logger.info { "Validation passed" }
        } else {
            logger.error { "Validation failed. Errors:" }
            errorTargetCollection.forEachIndexed { index, saxParseException ->
                logger.error { "${index + 1}: $saxParseException" }
            }
        }
        return errorTargetCollection
    }

    fun validateDocument(documentFile: File, validator: Validator) {
        validator.validate(getSource(documentFile))
    }

    fun getSource(file: File): StreamSource {
        return when (file.name.toLowerCase().split('.').last()) {
            "gz" -> StreamSource(GZIPInputStream(FileInputStream(file)))
            "zip" -> StreamSource(ZipInputStream(FileInputStream(file)))
            else -> StreamSource(FileInputStream(file))
        }

    }

    fun getNewErrorList() = mutableListOf<SAXParseException?>()
}

fun Path.isXsdAndNameContain(pattern: Regex) = fileName.toString().run {
    endsWith(".xsd") && contains(pattern)
}