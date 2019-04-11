package com.optum.giraffle.tasks

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
            myMap["addproperty"] = "gsqlAdminUsername"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Tigergraph Admin password:"
            myMap["addproperty"] = "gsqlAdminPassword"
            myMap["defaultvalue"] = "tigergraph"
            myAnt.invokeMethod("input", myMap)

            myMap["message"] = "Do you want to support multiple environments? "
            myMap["addproperty"] = "gsqlEnablePropertiesPlugin"
            myMap["defaultvalue"] = "y"
            myMap["validargs"] = "y,n"
            myAnt.invokeMethod("input", myMap)

            myAnt.setProperty("date", Date().toString())

            val propertiesForReplaceTokens: List<String> = listOf<String>("gsqlHost", "gsqlHost", "gsqlAdminUsername", "gsqlAdminPassword", "gsqlGraphname", "date")
            val tMap: Map<String, Any> = myAnt.properties.filterKeys { k ->
                propertiesForReplaceTokens.contains(k)
            }

            createFileFromResource("/config/gradle.properties", "gradle.properties", tMap)
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
        tokens: Map<String, Any> = mapOf<String, Any>()
    ) {
        val tempDir = Files.createTempDirectory("gsqlPluginConfig")
        val resourceDir = Files.createDirectories(tempDir.resolve("resource"))
        resourceDir.toFile().mkdirs()
        val outputDir = Files.createDirectories(tempDir.resolve("output"))
        outputDir.toFile().mkdirs()

        val resourceFile: File = Files.createFile(resourceDir.resolve(filename)).toFile()
        fillFileFromResource(resource, resourceFile)

        // Using gradle copy, its the easiest way to take advantage of ReplaceTokens
        project.copy {
            it.run {
                from(resourceDir)
                filter(mapOf("tokens" to tokens), ReplaceTokens::class.java)
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
