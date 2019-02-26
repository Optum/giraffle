package com.optum.gradle.tigergraph.tasks

// import org.apache.tools.ant.filters.ReplaceTokens
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
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

    /*
    @get:Input
    val tokens: Property<Map<String, String>> = extension.tokens
    */

    /**
     * The directory to use when outputting gsql sources.
     */
    @get:OutputDirectory
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

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
