package com.optum.gradle.tigergraph

// import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
// import org.gradle.api.provider.Property
// import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

open class GsqlCopySources : DefaultTask() {
    private val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

    @get:InputDirectory
    val inputDir: DirectoryProperty = project.objects.directoryProperty()

    /*
    @get:Input
    val tokens: Property<Map<String, String>> = extension.tokens
    */

    @get:OutputDirectory
    val outputDir: File = project.file("${project.buildDir}/${extension.scriptDir}")

    @TaskAction
    fun copyFiles() {
        // project.logger.lifecycle("tokens: ", tokens)

        project.copy {
            it.run {
                from(inputDir) {
                    include("**/*.gsql")
                    // filter(mapOf("tokens" to tokens), ReplaceTokens::class.java)
                }
                into(outputDir)
            }
        }
    }
}
