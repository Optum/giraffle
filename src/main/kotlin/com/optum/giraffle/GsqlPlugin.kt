package com.optum.giraffle

import com.optum.giraffle.Configurations.extensionName
import com.optum.giraffle.Configurations.gsqlRuntime
import com.optum.giraffle.Configurations.scriptDirectoryName
import com.optum.giraffle.tasks.GsqlCopySources
import com.optum.giraffle.tasks.GsqlShell
import com.optum.giraffle.tasks.GsqlTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.TaskProvider

open class GsqlPlugin : Plugin<Project> {
    /**
     * The name of the task that copies the GSQL source files into the build directory.
     *
     * @see com.optum.giraffle.tasks.GsqlCopySources
     */
    val copySourcesTaskName = "gsqlCopySources"

    /**
     * The name of the task that runs the interactive GSQL shell.
     *
     * @see com.optum.giraffle.tasks.GsqlShell
     */
    val gsqlShellTaskName = "gsqlShell"

    /**
     * The name of the task type for build scripts gsql tasks.
     *
     * @see com.optum.giraffle.tasks.GsqlTask
     */
    val gsqlTaskTypeName = "gsqlTaskType"

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        val gsqlPluginExtension = extensions.create(extensionName, GsqlPluginExtension::class.java, project)
        gsqlPluginExtension.scriptDir.convention(layout.projectDirectory.dir(scriptDirectoryName))
        gsqlPluginExtension.outputDir.convention(layout.buildDirectory.dir(scriptDirectoryName))
        gsqlPluginExtension.tokens.convention(emptyMap())

        val gsqlCopySources = registerGsqlCopySourcesTask(gsqlPluginExtension)
        registerGsqlShell()
        registerGsqlTask()

        logger.lifecycle("GSQL Plugin successfully applied to ${project.name}")

        tasks.withType(GsqlTask::class.java) { task ->
            task.dependsOn(gsqlCopySources)
        }

        configurations.maybeCreate(gsqlRuntime)
            .description = "Gsql Runtime for Tigergraph Plugin"

        dependencies.add(gsqlRuntime, "com.tigergraph.client:Driver:2.1.7")
        dependencies.add(gsqlRuntime, "commons-cli:commons-cli:1.4")
        dependencies.add(gsqlRuntime, "jline:jline:2.11")
        dependencies.add(gsqlRuntime, "org.json:json:20180130")
        dependencies.add(gsqlRuntime, "javax.xml.bind:jaxb-api:2.3.1")
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
}
