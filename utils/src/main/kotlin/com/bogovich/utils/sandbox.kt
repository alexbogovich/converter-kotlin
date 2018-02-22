package com.bogovich.utils

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors.groupingBy


fun main(args: Array<String>) {
    getAllSchemas("C:\\Users\\aleksandr.bogovich\\Desktop\\uspn\\Design&Analysis\\Technical Specification\\Альбом Форматов\\АФ 2.19.2д 17.01.2018\\Схемы").let {
        println("map = $it")
    }
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