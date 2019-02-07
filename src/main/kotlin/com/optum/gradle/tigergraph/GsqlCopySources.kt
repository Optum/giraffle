package com.optum.gradle.tigergraph

import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GsqlCopySources : DefaultTask() {
    private val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

    @InputDirectory
    val inputDir: File = project.file("${project.rootDir}/${extension.scriptDir}")

    @Input
    val tokens: Map<String, String> = extension.tokens

    @OutputDirectory
    val outputDir: File = project.file("${project.buildDir}/${extension.scriptDir}")

    init {
        group = "Development Tasks"
        description = "Copy gsql script from scriptDir to build directory."
    }

    @TaskAction
    fun copyFiles() {
        inputDir.apply { parentFile.mkdirs() }

        project.logger.lifecycle("tokens: ", tokens)

        project.copy {
            it.run {
                from(inputDir) {
                    include("**/*.gsql")
                    filter(mapOf("tokens" to tokens), ReplaceTokens::class.java)
                }
                into(outputDir)
            }
        }
    }
}
