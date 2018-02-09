package com.bogovich.xml.writer.dsl

import com.sun.xml.txw2.output.IndentingXMLStreamWriter
import mu.KLogging
import javax.xml.stream.XMLStreamWriter

class DslXMLStreamWriter(writer: XMLStreamWriter?) : IndentingXMLStreamWriter(writer) {
    companion object : KLogging()

    fun document(init: DslXMLStreamWriter.() -> Unit): DslXMLStreamWriter {
        this.writeStartDocument()
        this.init()
        this.writeEndDocument()
        this.flush()
        return this
    }

    fun element(name: String, init: DslXMLStreamWriter.() -> Unit): DslXMLStreamWriter {
        this.writeStartElement(name)
        this.init()
        this.writeEndElement()
        return this
    }

    fun element(namespace: String, name: String, init: DslXMLStreamWriter.() -> Unit): DslXMLStreamWriter {
        this.writeStartElement(namespace, name)
        this.init()
        this.writeEndElement()
        return this
    }

    fun defaultNamespace(namespace: String): DslXMLStreamWriter {
        this.writeDefaultNamespace(namespace)
        return this
    }

    fun namespace(prefix: String, namespace: String): DslXMLStreamWriter {
        this.writeNamespace(prefix, namespace)
        return this
    }

    fun element(name: String, content: Any) {
        element(name) {
            writeCharacters(content.toString())
        }
    }

    fun element(namespace: String, name: String, content: Any) {
        element(namespace, name) {
            writeCharacters(content.toString())
        }
    }

    fun attribute(name: String, value: Any) = writeAttribute(name, value.toString())

    infix fun String.tag(value: Any) {
        element(this, value)
    }

    infix fun String.tag(lambda: DslXMLStreamWriter.() -> Unit) {
        element(this, lambda)
    }

    infix fun Pair<String, String>.tag(value: Any) {
        element(this.first, this.second, value)
    }

    infix fun Pair<String, String>.tag(lambda: DslXMLStreamWriter.() -> Unit) {
        element(this.first, this.second, lambda)
    }

    infix fun String.attr(value: Any) {
        attribute(this, value)
    }
}