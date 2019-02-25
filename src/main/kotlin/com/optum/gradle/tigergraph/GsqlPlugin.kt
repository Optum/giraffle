package com.optum.gradle.tigergraph

import com.optum.gradle.tigergraph.Configurations.gsqlRuntime
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
        gsqlPluginExtension.scriptDir.set(layout.projectDirectory.dir(DEFAULT_GSQL_SCRIPT_DIR))
        gsqlPluginExtension.outputDir.set(layout.buildDirectory.dir(DEFAULT_GSQL_SCRIPT_DIR))

        registerGsqlCopySourcesTask(gsqlPluginExtension)
        registerGsqlTask(gsqlPluginExtension)

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
            configurations.maybeCreate(gsqlRuntime)
                    .description = "Gsql Runtime for Tigergraph Plugin"

            dependencies.add(gsqlRuntime, "com.tigergraph.client:Driver:2.1.7")
            dependencies.add(gsqlRuntime, "commons-cli:commons-cli:1.4")
            dependencies.add(gsqlRuntime, "jline:jline:2.11")
            dependencies.add(gsqlRuntime, "org.json:json:20180130")
            dependencies.add(gsqlRuntime, "javax.xml.bind:jaxb-api:2.3.1")
        }
    }

    private fun Project.registerGsqlCopySourcesTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlCopySources> =
            tasks.register(COPY_SOURCES_TASK_NAME, GsqlCopySources::class.java) { gsqlCopySources ->
                gsqlCopySources.group = JavaBasePlugin.BUILD_TASK_NAME
                gsqlCopySources.description = "Copy gsql scripts from input directory to build directory prior to execution."
                gsqlCopySources.inputDir.set(gsqlPluginExtension.scriptDir)
                gsqlCopySources.outputDir.set(gsqlPluginExtension.outputDir)
                // gsqlCopySources.outputDir
                // gsqlCopySources.tokens.set(gsqlPluginExtension.tokens)
            }

    private fun Project.registerGsqlTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlTask> =
            tasks.register(GSQL_SHELL_TASK_NAME, GsqlTask::class.java) { gsqlShell ->
                gsqlShell.dependsOn(COPY_SOURCES_TASK_NAME)
                gsqlShell.connectionData.setAdminUserName(gsqlPluginExtension.adminUserName)
                gsqlShell.connectionData.setAdminPassword(gsqlPluginExtension.adminPassword)
                gsqlShell.connectionData.setUserName(gsqlPluginExtension.userName)
                gsqlShell.connectionData.setPassword(gsqlPluginExtension.password)
                gsqlShell.connectionData.setServerName(gsqlPluginExtension.serverName)
            }
}
