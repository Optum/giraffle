package com.optum.gradle.tigergraph.tasks

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GsqlTask() : GsqlAbstract() {

    @Input
    lateinit var scriptPath: String

    @TaskAction
    override fun exec() {
        args = buildArgs()
        logger.info("Args: $args")
        super.exec()
    }

    override fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()
        val outputDir: DirectoryProperty = gsqlPluginExtension.outputDir

        newArgs.add("--ip")
        newArgs.add(connectionData.getServerName())
        newArgs += determineUser(superUser)

        newArgs.add("${outputDir.get()}/$scriptPath")

        return newArgs
    }
}
