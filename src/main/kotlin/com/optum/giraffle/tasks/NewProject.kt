package com.optum.giraffle.tasks

import com.optum.giraffle.Configurations.net_saliman_properties_version
import org.apache.tools.ant.filters.ReplaceTokens
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
        project.ant { myAnt ->
            myAnt.invokeMethod("echo", "Welcome to the new project wizard. Please answer the following questions to start a new project.")
            val myMap: MutableMap<String, String> = mutableMapOf<String, String>(
                "message" to "Project Name:",
                "addproperty" to "gsqlGraphname",
                "defaultvalue" to "myApp"
            )
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph Host:"
            myMap["addproperty"] = "gsqlHost"
            myMap["defaultvalue"] = "localhost"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph Admin username:"
            myMap["addproperty"] = "gsqlAdminUserName"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph Admin password:"
            myMap["addproperty"] = "gsqlAdminPassword"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph username:"
            myMap["addproperty"] = "gsqlUserName"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph password:"
            myMap["addproperty"] = "gsqlPassword"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Do you want to support multiple environments? "
            myMap["addproperty"] = "gsqlEnablePropertiesPlugin"
            myMap["defaultvalue"] = "y"
            myMap["validargs"] = "y,n"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Do you want to use the Kotlin or Groovy DSL for you build?"
            myMap["addproperty"] = "kotlinOrGroovy"
            myMap["defaultvalue"] = "k"
            myMap["validargs"] = "k,g"
            myAnt.invokeMethod("input", myMap)

            myAnt.setProperty("date", Date().toString())

            val propertiesForReplaceTokens: List<String> = listOf<String>(
                "gsqlHost",
                "gsqlAdminUserName",
                "gsqlAdminPassword",
                "gsqlUserName",
                "gsqlPassword",
                "gsqlGraphname",
                "date"
            )
            val tMap: Map<String, Any> = myAnt.properties.filterKeys { k ->
                propertiesForReplaceTokens.contains(k)
            }

            val usePropPlugin: Boolean =
                when (myAnt.getProperty("gsqlEnablePropertiesPlugin")) {
                    "y" -> true
                    else -> false
                }

            createFileFromResource("/properties/gradle.properties", "gradle.properties", tMap)

            when (myAnt.getProperty("kotlinOrGroovy")) {
                "k" -> createFileFromResource("/kotlin/build.gradle.kts", "build.gradle.kts", emptyMap())
                "g" -> createFileFromResource("/groovy/build.gradle", "build.gradle", emptyMap())
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
        newTokens["propertiesPlugin"] = if (enableProperties) "\nid(\"net.saliman.properties\") version \"$net_saliman_properties_version\"" else ""

        // Using gradle copy, its the easiest way to take advantage of ReplaceTokens
        project.copy {
            it.run {
                from(resourceDir)
                filter(mapOf("tokens" to newTokens), ReplaceTokens::class.java)
                into(outputDir)
            }
        }

        val destFile: File = File(project.projectDir, filename)
        // We'll make a backup if the destination file already exists
        if (destFile.exists()) {
            destFile.copyTo(
            File(project.projectDir, "backup-$filename"), overwrite = true)
        }
        File(outputDir.toFile(), filename).copyTo(destFile, overwrite = true)
    }
}
