package com.optum.gradle.tigergraph

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

open class GsqlPluginExtension(project: Project) {
    val scriptDir: DirectoryProperty = project.objects.directoryProperty()
    var tokens: Map<String, String> = emptyMap<String, String>()
    var serverName: String = "localhost"
    var userName: String = "tigergraph"
    var password: String = "tigergraph"
    var adminUserName: String? = null
    var adminPassword: String? = null
}
