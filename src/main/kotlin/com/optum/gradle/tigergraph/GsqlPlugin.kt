package com.optum.gradle.tigergraph

import org.gradle.api.Plugin
import org.gradle.api.Project

open class GsqlPlugin : Plugin<Project> {
    /**
     * The name of the extension for configuring the runtime behavior of the plugin.
     *
     * @see com.optum.gradle.tigergraph.GsqlPluginExtension
     */
    val EXTENSION_NAME = "tigergraph"

    /**
     * The name of the task that copies the GSQL source files into the build directory.
     *
     * @see com.optum.gradle.tigergraph.GsqlCopySources
     */
    val COPY_SOURCES_TASK_NAME = "gsqlCopySources"

    /**
     * The name of the task that runs the interactive GSQL shell.
     *
     * @see com.optum.gradle.tigergraph.GsqlShell
     */
    val GSQL_SHELL_TASK_NAME = "gsqlShell"

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        extensions.create(EXTENSION_NAME, GsqlPluginExtension::class.java)

        // Create CopySources task
        val gsqlCopySources = project.tasks.run {
            create(COPY_SOURCES_TASK_NAME, GsqlCopySources::class.java)
        }
        project.tasks.run {
            create(GSQL_SHELL_TASK_NAME, GsqlShell::class.java)
        }

        with(project) {
            logger.lifecycle("GSQL Plugin successfully applied to ${project.name}")
            tasks.withType(GsqlTask::class.java) { task ->
                logger.info("${task.name}: is of type GsqlTask")
                task.dependsOn(gsqlCopySources)
            }
        }
    }
}
