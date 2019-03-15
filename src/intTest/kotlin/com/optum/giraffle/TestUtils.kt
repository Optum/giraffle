package com.optum.giraffle

import java.io.File

fun File.fillFromResource(resourceName: String) {
    ClassLoader.getSystemResourceAsStream(resourceName).use { inputStream ->
        outputStream().use { inputStream.copyTo(it) }
    }
}
