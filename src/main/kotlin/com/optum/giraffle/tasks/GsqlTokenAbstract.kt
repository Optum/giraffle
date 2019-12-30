package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
import com.optum.giraffle.UriScheme
import okhttp3.HttpUrl
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input

abstract class GsqlTokenAbstract() : DefaultTask() {
    @Input
    protected val gsqlPluginExtension: GsqlPluginExtension =
        project.extensions.getByType(GsqlPluginExtension::class.java)

    @Input
    protected val uriScheme: UriScheme = gsqlPluginExtension.uriScheme.get()

    @Input
    protected val host: String

    @Input
    protected val defaultPort: Int

    init {
        this.host = gsqlPluginExtension.serverName.get()
        this.defaultPort = gsqlPluginExtension.restPort.get()
    }

    @Input
    fun getUrl(): String = getHttpUrl().toString()

    @Input
    fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
            .scheme(uriScheme.scheme)
            .host(host)
            .port(defaultPort)
            .addPathSegment("requesttoken")
            .build()
    }
}
