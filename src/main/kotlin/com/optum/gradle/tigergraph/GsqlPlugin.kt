package com.optum.gradle.tigergraph

import org.gradle.api.Plugin
import org.gradle.api.Project

open class GsqlPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        extensions.create("tigergraph", GsqlPluginExtension::class.java)

        // Create CopySources task
        val gsqlCopySources = project.tasks.run {
            create("gsqlCopySources", GsqlCopySources::class.java)
        }
        project.tasks.run {
            create("gsqlShell", GsqlShell::class.java)
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
