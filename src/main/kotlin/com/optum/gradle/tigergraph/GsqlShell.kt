package com.optum.gradle.tigergraph

import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskAction

open class GsqlShell() : GsqlAbstract() {
    init {
        group = "GSQL Shell"
        description = "Invokes a gsql shell for executing ad-hoc gsql commands."
    }
    @TaskAction
    override fun exec() {
        val cfg: Configuration? = project.configurations.findByName("tigergraph")

        if (cfg != null) {
            classpath = cfg
        }

        main = "com.tigergraph.client.Driver"
        standardInput = System.`in`
        args = buildArgs()
        logger.info("Args: $args")
        super.exec()
    }

    override fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()

        newArgs.add("--ip")
        newArgs.add(connectionData.getServerName())
        newArgs += determineUser(superUser)

        return newArgs
    }
}
