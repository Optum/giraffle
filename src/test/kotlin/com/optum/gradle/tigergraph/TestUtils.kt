package com.optum.gradle.tigergraph

import java.io.File

fun File.fillFromResource(resourceName: String) {
    ClassLoader.getSystemResourceAsStream(resourceName).use { inputStream ->
        outputStream().use { inputStream.copyTo(it) }
    }
}
