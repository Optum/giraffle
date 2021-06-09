package com.optum.giraffle.tasks

import com.optum.giraffle.Configurations.net_saliman_properties_version
import org.apache.tools.ant.DirectoryScanner
import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.AntBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import java.util.Date

open class NewProject : DefaultTask() {
    /**
     * Use ant input to get values from the user. Use this data to generate properties and build files.
     */
    @TaskAction
    fun newProject() {
        project.ant {
            with(it) {
                invokeMethod(
                    "echo",
                    "Welcome to the new project wizard. Please answer the following questions to start a new project."
                )

                getInput(
                    message = "Project Name:",
                    propertyName = "gsqlGraphname",
                    defaultValue = "myApp"
                )
                getInput(
                    message = "Tigergraph Host:",
                    propertyName = "gsqlHost",
                    defaultValue = "localhost"
                )
                getInput(
                    message = "Tigergraph Admin username:",
                    propertyName = "gsqlAdminUserName",
                    defaultValue = "tigergraph"
                )
                getInput(
                    message = "Tigergraph Admin password:",
                    propertyName = "gsqlAdminPassword",
                    defaultValue = "tigergraph"
                )
                getInput(
                    message = "Tigergraph username:",
                    propertyName = "gsqlUserName",
                    defaultValue = "tigergraph"
                )
                getInput(
                    message = "Tigergraph password:",
                    propertyName = "gsqlPassword",
                    defaultValue = "tigergraph"
                )
                getInput(
                    message = "Do you want to support multiple environments?",
                    propertyName = "gsqlEnablePropertiesPlugin",
                    defaultValue = "y",
                    validArgs = listOf("y", "n")
                )
                getInput(
                    message = "Do you want to use the Kotlin or Groovy DSL for you build?",
                    propertyName = "kotlinOrGroovy",
                    defaultValue = "k",
                    validArgs = listOf("k", "g")
                )

                setProperty("date", Date().toString())
            }

            val propertiesForReplaceTokens: List<String> = listOf<String>(
                "gsqlHost",
                "gsqlAdminUserName",
                "gsqlAdminPassword",
                "gsqlUserName",
                "gsqlPassword",
                "gsqlGraphname",
                "date"
            )

            val credentialMap: Map<String, Any> = it.properties.filterKeys { k ->
                propertiesForReplaceTokens.contains(k)
            }

            val usePropPlugin: Boolean =
                when (it.getProperty("gsqlEnablePropertiesPlugin")) {
                    "y" -> true
                    else -> false
                }

            createFileFromResource("/properties/gradle-local.properties", "gradle-local.properties", credentialMap)
            createFileFromResource("/properties/gradle.properties", "gradle.properties", credentialMap)
            createFileFromResource("/git/gitignore", ".gitignore")

            when (it.getProperty("kotlinOrGroovy")) {
                "k" -> createFileFromResource("/kotlin/build.gradle.kts", "build.gradle.kts", emptyMap(), usePropPlugin)
                "g" -> createFileFromResource("/groovy/build.gradle", "build.gradle", emptyMap(), usePropPlugin)
            }

            val skeletonPaths: List<String> = listOf<String>(
                "./db_scripts/",
                "./db_scripts/schema/",
                "./db_scripts/query/",
                "./db_scripts/roles/",
                "./db_scripts/load/",
                "./db_scripts/load/create/"
            )
            skeletonPaths.map { fileName ->
                when (File(fileName).mkdirs()) {
                    true -> project.logger.info("Directory creation successful for: $fileName")
                    false -> project.logger.info("Director creation failed for: $fileName")
                }
            }
        }
    }

    /**
     * Takes a resource value relative to the JAR and puts that contents into the file
     */
    private fun fillFileFromResource(resourceName: String, filename: File) =
        javaClass.getResourceAsStream(resourceName).use { inputStream ->
            filename.outputStream().use { os ->
                inputStream.copyTo(os)
            }
        }

    /**
     * Creates a file from a resource value, using ReplaceTokens to perform token replacement
     */
    private fun createFileFromResource(
        resource: String,
        filename: String,
        tokens: Map<String, Any> = mapOf<String, Any>(),
        enableProperties: Boolean = true
    ) {
        val tempDir = Files.createTempDirectory("gsqlPluginConfig")
        val resourceDir = Files.createDirectories(tempDir.resolve("resource"))
        resourceDir.toFile().mkdirs()
        val outputDir = Files.createDirectories(tempDir.resolve("output"))
        outputDir.toFile().mkdirs()

        val resourceFile: File = Files.createFile(resourceDir.resolve(filename)).toFile()
        fillFileFromResource(resource, resourceFile)

        val newTokens: MutableMap<String, Any> = tokens.toMutableMap<String, Any>()
        newTokens["propertiesPlugin"] = if (enableProperties) "\n    id(\"net.saliman.properties\") version \"$net_saliman_properties_version\"" else ""

        // Using gradle copy, its the easiest way to take advantage of ReplaceTokens

        project.copy {
            it.run {
                DirectoryScanner.removeDefaultExclude("**/.gitignore")

                from(resourceDir)
                filter(mapOf("tokens" to newTokens), ReplaceTokens::class.java)
                into(outputDir)
            }
        }

        val destFile: File = File(project.projectDir, filename)
        // We'll make a backup if the destination file already exists
        if (destFile.exists()) {
            destFile.copyTo(
                File(project.projectDir, "backup-$filename"), overwrite = true
            )
        }
        File(outputDir.toFile(), filename).copyTo(destFile, overwrite = true)
    }

    /**
     * Get input from user if ant properties are not defined
     */
    private fun AntBuilder.getInput(message: String, propertyName: String, defaultValue: String, validArgs: List<String>? = null) =
        with(this) {
            if (!properties.containsKey(propertyName)) {
                val args = mutableMapOf("message" to message, "addproperty" to propertyName, "defaultvalue" to defaultValue)
                if (validArgs != null) args["validargs"] = validArgs.joinToString(separator = ",")
                invokeMethod(
                    "input", args
                )
            }
        }
}
