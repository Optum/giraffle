package com.optum.gradle.tigergraph

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

open class GsqlPluginExtension(project: Project) {
    val scriptDir: DirectoryProperty = project.objects.directoryProperty()
    var tokens: Map<String, String> = emptyMap<String, String>()
    var serverName: Property<String> = project.objects.property(String::class.java)
    var userName: Property<String> = project.objects.property(String::class.java)
    var password: Property<String> = project.objects.property(String::class.java)
    var adminUserName: Property<String> = project.objects.property(String::class.java)
    var adminPassword: Property<String> = project.objects.property(String::class.java)
}
