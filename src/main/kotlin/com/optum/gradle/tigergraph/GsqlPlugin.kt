package com.optum.gradle.tigergraph

import org.gradle.api.Plugin
import org.gradle.api.Project

open class GsqlPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        extensions.create("tigergraph", GsqlPluginExtension::class.java)

        // Create CopySources task
        project.tasks.run {
            create("gsqlCopySources", GsqlCopySources::class.java) {
                it.group = "Development"
            }
        }

        with(project) {
            logger.lifecycle("GSQL Plugin successfully applied to {$project.name}")
        }
    }
}
