package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
import com.optum.giraffle.UriScheme
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

abstract class GsqlTokenAbstract() : DefaultTask() {
    @Input
    protected val gsqlPluginExtension: GsqlPluginExtension =
        project.extensions.getByType(GsqlPluginExtension::class.java)

    // @Input
    // var useHttps: Boolean = false
    @Input
    protected val uriScheme: UriScheme = gsqlPluginExtension.uriScheme.get()

    @Input
    protected val host: String

    @Input
    protected val defaultPort: String

    init {
        this.host = gsqlPluginExtension.serverName.get()
        this.defaultPort = gsqlPluginExtension.restPort.get()
    }

    fun url(): String = "${uriScheme.getPrefix()}${this.host}:${this.defaultPort}/requesttoken"
}
