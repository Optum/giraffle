package com.optum.gradle.tigergraph

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider

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

    val DEFAULT_GSQL_SCRIPT_DIR = "db_scripts"

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        val gsqlPluginExtension = extensions.create(EXTENSION_NAME, GsqlPluginExtension::class.java, project)
        gsqlPluginExtension.scriptDir.set(
                layout.projectDirectory.dir(DEFAULT_GSQL_SCRIPT_DIR)
        )

        registerGsqlCopySourcesTask(gsqlPluginExtension)

        // Create CopySources task
        /*
        project.tasks.run {
            create(GSQL_SHELL_TASK_NAME, GsqlShell::class.java)
        }
        */

        with(project) {
            logger.lifecycle("GSQL Plugin successfully applied to ${project.name}")
            /*
            tasks.withType(GsqlTask::class.java) { task ->
                logger.info("${task.name}: is of type GsqlTask")
                task.dependsOn(gsqlCopySources)
            }
            */
        }
    }

    private fun Project.registerGsqlCopySourcesTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlCopySources> =
            tasks.register(COPY_SOURCES_TASK_NAME, GsqlCopySources::class.java) { gsqlCopySources ->
                gsqlCopySources.group = JavaBasePlugin.BUILD_TASK_NAME
                gsqlCopySources.description = "Copy gsql scripts from input directory to build directory prior to execution."
                gsqlCopySources.inputDir.set(gsqlPluginExtension.scriptDir)
                // gsqlCopySources.outputDir
                // gsqlCopySources.tokens.set(gsqlPluginExtension.tokens)
            }
}
