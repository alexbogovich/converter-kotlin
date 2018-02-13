package com.bogovich.xml.writer.dsl

typealias xmlStreamLambda =  DslXMLStreamWriter.() -> Unit
typealias xmlStreamCoroutine =  suspend CoroutineXMLStreamWriter.() -> Unit