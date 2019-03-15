package com.optum.giraffle.tasks

import org.apache.tools.ant.filters.ReplaceTokens
import com.optum.giraffle.Configurations.scriptDirectoryName
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

open class GsqlCopySources : DefaultTask() {
    /**
     * The directory to use as our gsql script source directory.
     *
     */
    @get:InputDirectory
    val inputDir: DirectoryProperty = project.objects.directoryProperty()

    @get:Input
    val tokens: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    /**
     * The directory to use when outputting gsql sources.
     */
    @get:OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    @TaskAction
    fun copyFiles() {
        outputDir.set(project.layout.buildDirectory.dir(scriptDirectoryName))
        // project.logger.lifecycle("tokens: ", tokens)
        tokens.get().forEach { entry ->
            logger.quiet("${entry.key}: ${entry.value}")
        }

        // inputs.properties(tokens)
        project.copy {
            it.run {
                from(inputDir) {
                    include("**/*.gsql")
                    filter(mapOf("tokens" to tokens.get()), ReplaceTokens::class.java)
                }
                into(outputDir)
            }
        }
    }
}
