package com.optum.giraffle.tasks

import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

open class GsqlTask() : GsqlAbstract() {

    @Input
    @Optional
    var scriptPath: String? = null

    @Input
    @Optional
    var scriptCommand: String? = null

    @Input
    @Optional
    var graphName: String? = null

    @Input
    var useGlobal: Boolean = false

    @TaskAction
    override fun exec() {
        // Determine whether using scriptPath or scriptCommand
        val decide: (String?, String?) -> List<String> = { sp: String?, sc: String? ->
            when {
                sp != null -> buildArgs()
                sc != null -> {
                    sc.let {
                        scriptPath = createGsqlScript(it).toString()
                        buildArgs()
                    }
                }
                else -> throw GradleException("Either scriptPath or scriptCommand must be specified.")
            }
        }

        args = decide(scriptPath, scriptCommand)

        val argOutput = args
        logger.info(
            "Args: ${
            argOutput!!.map {
                when (it == this.connectionData.getPassword() || it == this.connectionData.getAdminPassword()) {
                    true -> "********"
                    false -> it
                }
            }
            }"
        )
        super.exec()
    }

    override fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()
        val outputDir: DirectoryProperty = gsqlPluginExtension.outputDir

        newArgs.add("--ip")
        newArgs.add(connectionData.getServerName())
        newArgs += determineUser(superUser)

        // Determine if we're setting the sub-graph on the command line
        val graphToAssign: String? = connectionData.getGraphName() ?: graphName
        when (!useGlobal && graphToAssign != null) {
            true -> {
                newArgs.add("-g")
                newArgs.add(graphToAssign)
            }
        }

        newArgs += getCaCert()
        newArgs += getLogDir()
        newArgs += getGraphStudio()

        newArgs.add("-f")
        newArgs.add("${outputDir.get()}/$scriptPath")

        return newArgs
    }

    private fun createGsqlScript(scriptContent: String): Path {
        // outputDir is where all the build scripts end up
        val outputDir: DirectoryProperty = gsqlPluginExtension.outputDir

        // create a temporary directory in the outputDir
        val dir: Path = Files.createTempDirectory(Paths.get(outputDir.get().toString()), "giraffle")
        val scriptFile: Path = Files.createFile(dir.resolve("command.gsql"))
        scriptFile.toFile().writeText(scriptContent)
        // Return the path to the file relative to the outputDir
        return Paths.get(outputDir.get().toString()).relativize(scriptFile)
    }
}
