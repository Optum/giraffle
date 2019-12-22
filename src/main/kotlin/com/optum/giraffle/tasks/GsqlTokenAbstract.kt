package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

abstract class GsqlTokenAbstract() : DefaultTask() {
    @Input
    protected val gsqlPluginExtension: GsqlPluginExtension =
        project.extensions.getByType(GsqlPluginExtension::class.java)

    @get:Input
    lateinit var secret: String

    @Input
    var useHttps: Boolean = false

    @Input
    protected val host: String

    @Input
    protected val defaultPort: String

    init {
        this.host = gsqlPluginExtension.serverName.get()
        this.defaultPort = gsqlPluginExtension.restPort.get()
    }

    fun urlPrefix(): String = when (useHttps) {
        true -> "https://"
        false -> "http://"
    }

    fun url(): String = "${urlPrefix()}${this.host}:${this.defaultPort}/requesttoken"
}
