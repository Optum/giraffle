package com.optum.giraffle.tasks

import org.gradle.api.tasks.TaskAction

open class GsqlShell() : GsqlAbstract() {
    init {
        group = "GSQL Shell"
        description = "Invokes a gsql shell for executing ad-hoc gsql commands."
    }
    @TaskAction
    override fun exec() {
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
