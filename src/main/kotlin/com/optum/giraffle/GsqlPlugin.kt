package com.optum.giraffle

import com.optum.giraffle.Configurations.extensionName
import com.optum.giraffle.Configurations.gsqlRuntime
import com.optum.giraffle.Configurations.gsql_client_version
import com.optum.giraffle.Configurations.gsql_port
import com.optum.giraffle.Configurations.host_value
import com.optum.giraffle.Configurations.rest_pp_port
import com.optum.giraffle.Configurations.scriptDirectoryName
import com.optum.giraffle.tasks.GsqlCopySources
import com.optum.giraffle.tasks.GsqlShell
import com.optum.giraffle.tasks.GsqlTask
import com.optum.giraffle.tasks.GsqlTokenDeleteTask
import com.optum.giraffle.tasks.GsqlTokenTask
import com.optum.giraffle.tasks.NewProject
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider

open class GsqlPlugin : Plugin<Project> {
    /**
     * The name of the task that copies the GSQL source files into the build directory.
     *
     * @see com.optum.giraffle.tasks.GsqlCopySources
     */
    private val copySourcesTaskName = "gsqlCopySources"

    /**
     * The name of the task that runs the interactive GSQL shell.
     *
     * @see com.optum.giraffle.tasks.GsqlShell
     */
    private val gsqlShellTaskName = "gsqlShell"

    /**
     * The name of the task type for build scripts gsql tasks.
     *
     * @see com.optum.giraffle.tasks.GsqlTask
     */
    private val gsqlTaskTypeName = "gsqlTaskType"

    /**
     * The name of the task that gets a token.
     *
     * @see com.optum.giraffle.tasks.GsqlTokenTask
     */
    private val gsqlTokenTaskName = "gsqlToken"

    /**
     * The name of the task that deletes a token.
     *
     * @see com.optum.giraffle.tasks.GsqlTokenDeleteTask
     */
    private val gsqlDeleteTokenTask = "gsqlDeleteToken"

    private val authGroup = "Tigergraph Authentication"

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        val gsqlPluginExtension = extensions.create(extensionName, GsqlPluginExtension::class.java, project)
        gsqlPluginExtension.scriptDir.set(layout.projectDirectory.dir(scriptDirectoryName))
        gsqlPluginExtension.outputDir.set(layout.buildDirectory.dir(scriptDirectoryName))
        gsqlPluginExtension.tokens.set(emptyMap())
        gsqlPluginExtension.restPort.set(rest_pp_port)
        gsqlPluginExtension.gsqlPort.set(gsql_port)
        gsqlPluginExtension.uriScheme.set(UriScheme.HTTP)
        gsqlPluginExtension.serverName.set(host_value)

        val gsqlCopySources = registerGsqlCopySourcesTask(gsqlPluginExtension)
        registerGsqlShell()
        registerGsqlTask()
        registerNewProject()
        registerTokenTask(gsqlPluginExtension)
        registerDeleteTokenTask(gsqlPluginExtension)

        logger.lifecycle("GSQL Plugin successfully applied to ${project.name}")

        tasks.withType(GsqlTask::class.java) { task ->
            task.dependsOn(gsqlCopySources)
        }

        val config: Configuration = configurations.create(gsqlRuntime)
            .setVisible(false)
            .setDescription("Gsql Runtime for Tigergraph Plugin")

        config.defaultDependencies(Action<DependencySet>() {
            it.add(project.dependencies.create("com.tigergraph.client:gsql_client:$gsql_client_version"))
        })
        // dependencies.add(gsqlRuntime, "com.tigergraph.client:gsql_client:$gsql_client_version")
    }

    private fun Project.registerGsqlCopySourcesTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlCopySources> =
            tasks.register(copySourcesTaskName, GsqlCopySources::class.java) { gsqlCopySources ->
                gsqlCopySources.group = JavaBasePlugin.BUILD_TASK_NAME
                gsqlCopySources.description = "Copy gsql scripts from input directory to build directory prior to execution."
                gsqlCopySources.inputDir.set(gsqlPluginExtension.scriptDir)
                gsqlCopySources.outputDir.set(gsqlPluginExtension.outputDir) // This isn't overridable at the moment. Should it be a property?
                gsqlCopySources.tokens.putAll(gsqlPluginExtension.tokens)
            }

    private fun Project.registerGsqlTask(): TaskProvider<GsqlTask> =
            tasks.register(gsqlTaskTypeName, GsqlTask::class.java)

    private fun Project.registerGsqlShell(): TaskProvider<GsqlShell> =
            tasks.register(gsqlShellTaskName, GsqlShell::class.java) { gsqlShell ->
                gsqlShell.group = "GSQL Interactive"
                gsqlShell.description = "Run an interactive gsql shell session"
            }
    private fun Project.registerNewProject(): TaskProvider<NewProject> =
        tasks.register("gsqlNewProject", NewProject::class.java) { newProject ->
            newProject.group = "GSQL Project Wizard"
            newProject.description = "Create scaffolding for new project"
        }

    private fun Project.registerTokenTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlTokenTask> =
        tasks.register(gsqlTokenTaskName, GsqlTokenTask::class.java) { gsqlToken ->
            gsqlToken.group = authGroup
            gsqlToken.description = "Uses Tigergraph's REST endpoint to obtain an OAUTH token"
            gsqlToken.host.set(gsqlPluginExtension.serverName)
            gsqlToken.uriScheme.set(gsqlPluginExtension.uriScheme)
            gsqlToken.defaultPort.set(gsqlPluginExtension.restPort)
        }

    private fun Project.registerDeleteTokenTask(gsqlPluginExtension: GsqlPluginExtension): TaskProvider<GsqlTokenDeleteTask> =
        tasks.register(gsqlDeleteTokenTask, GsqlTokenDeleteTask::class.java) { gsqlDeleteToken ->
            gsqlDeleteToken.group = authGroup
            gsqlDeleteToken.description = "Uses Tigergraph's REST end point to delete an OAUTH token"
            gsqlDeleteToken.host.set(gsqlPluginExtension.serverName)
            gsqlDeleteToken.uriScheme.set(gsqlPluginExtension.uriScheme)
            gsqlDeleteToken.defaultPort.set(gsqlPluginExtension.restPort)
        }
}
