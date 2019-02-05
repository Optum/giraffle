package com.optum.gradle.tigergraph

import org.gradle.api.Project
import org.gradle.api.Plugin

open class GsqlPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = project.run {
        // Register extension for dsl
        extensions.create("tigergraph", GsqlPluginExtension::class.java)

        with(project) {
            logger.lifecycle("GSQL Plugin successfully applied to {$project.name}")
        }
    }
}
