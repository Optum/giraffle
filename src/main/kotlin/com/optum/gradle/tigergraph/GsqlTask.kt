package com.optum.gradle.tigergraph

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GsqlTask() : GsqlAbstract() {

    @Input
    lateinit var scriptPath: String

    @TaskAction
    override fun exec() {

        val cfg: Configuration? = project.configurations.findByName("tigergraph")

        if (cfg != null) {
            classpath = cfg
        }

        main = "org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader"
        args = buildArgs()
        logger.info("Args: $args")
        super.exec()
    }

    override fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()

        newArgs.add("--ip")
        newArgs.add(extension.serverName)
        newArgs += determineUser(superUser)

        newArgs.add("${project.buildDir}/$scriptPath")

        return newArgs
    }
}
