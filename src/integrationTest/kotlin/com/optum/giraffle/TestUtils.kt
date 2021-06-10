package com.optum.giraffle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import java.nio.file.Path

fun File.fillFromResource(resourceName: String) {
    ClassLoader.getSystemResourceAsStream(resourceName).use { inputStream ->
        outputStream().use { inputStream.copyTo(it) }
    }
}

fun Path.printFiles(): String {
    return this.toFile().walkTopDown().fold("") {
        acc: String, file: File ->
        "$acc\n$file"
    }
}

fun execute(projectDir: File, vararg arguments: String): BuildResult {
    return GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments(arguments.toList())
        .withPluginClasspath()
        .build()
}
