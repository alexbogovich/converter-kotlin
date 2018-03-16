package com.bogovich.xml.writer.dsl

import com.sun.xml.txw2.output.IndentingXMLStreamWriter
import mu.KLogging
import java.io.OutputStream
import java.io.Writer
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter


class DslXMLStreamWriter(writer: XMLStreamWriter?) : IndentingXMLStreamWriter(writer) {
    companion object : KLogging()
    constructor(writer: Writer) : this(XMLOutputFactory.newFactory().createXMLStreamWriter(writer))
    constructor(stream: OutputStream) : this(XMLOutputFactory.newFactory().createXMLStreamWriter(stream, "UTF-8"))

    private val namespaceMapping = HashMap<String, String>()

    fun document(init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartDocument()
        this.init()
        this.writeEndDocument()
        this.flush()
        return this
    }

    private fun element(name: String, init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartElement(name)
        this.init()
        this.writeEndElement()
        return this
    }

    private fun element(namespace: String, tagName: String, init: xmlStreamLambda): DslXMLStreamWriter {
        this.writeStartElement(namespace, tagName)
        this.init()
        this.writeEndElement()
        return this
    }

    fun defaultNamespace(namespace: String): DslXMLStreamWriter {
        this.writeDefaultNamespace(namespace)
        return this
    }

    fun namespace(prefix: String, namespace: String): DslXMLStreamWriter {
        namespaceMapping[prefix] = namespace
        this.writeNamespace(prefix, namespace)
        return this
    }

    private fun element(name: String, content: Any) {
        element(name) {
            writeCharacters(content.toString())
        }
    }

    private fun element(namespace: String, name: String, content: Any) {
        element(namespace, name) {
            writeCharacters(content.toString())
        }
    }

    fun attribute(name: String, value: Any) = writeAttribute(name, value.toString())

    infix fun String.tag(value: Any) {
        element(this, value)
    }

    infix operator fun String.invoke(value: Any) {
        if (this.contains(":")) {
            val tag = this.split(":")
            element(namespaceMapping[tag[0]]!!, tag[1], value)
        } else {
            element(this, value)
        }
    }

    infix operator fun String.invoke(lambda: xmlStreamLambda) {
        if (this.contains(":")) {
            val tag = this.split(":")
            if (!namespaceMapping.contains(tag[0])) {
                throw RuntimeException("Prefix ${tag[0]} not in $namespaceMapping")
            }
            element(namespaceMapping[tag[0]]!!, tag[1], lambda)
        } else {
            element(this,lambda)
        }
    }

    infix fun String.tag(lambda: xmlStreamLambda) {
        element(this, lambda)
    }

    infix fun Pair<String, String>.tag(value: Any) {
        element(this.first, this.second, value)
    }

    infix fun Pair<String, String>.tag(lambda: xmlStreamLambda) {
        element(this.first, this.second, lambda)
    }

    infix fun String.attr(value: Any) {
        attribute(this, value)
    }
}