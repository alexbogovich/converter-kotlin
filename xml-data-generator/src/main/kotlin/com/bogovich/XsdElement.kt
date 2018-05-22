package com.bogovich

import java.nio.file.Path

data class XsdElement(val id: String, val name: String, val xsdPath: Path, val xsdNamespace: String)