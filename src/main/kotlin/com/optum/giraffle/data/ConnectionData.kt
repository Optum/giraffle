package com.optum.giraffle.data

import com.optum.giraffle.Configurations.rest_pp_port
import com.optum.giraffle.UriScheme
import java.io.Serializable
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class ConnectionData(project: Project) {
    private val userName: Property<String> = project.objects.property(String::class.java)
    private val adminUserName: Property<String> = project.objects.property(String::class.java)
    private val password: Property<String> = project.objects.property(String::class.java)
    private val adminPassword: Property<String> = project.objects.property(String::class.java)
    private val serverName: Property<String> = project.objects.property(String::class.java)
    private val graphName: Property<String> = project.objects.property(String::class.java)
    private val restPort: Property<String> = project.objects.property(String::class.java)
    private val gsqlClientVersion: Property<String> = project.objects.property(String::class.java)
    private val caCert: Property<String> = project.objects.property(String::class.java)
    private val logDir: Property<String> = project.objects.property(String::class.java)
    private val uriScheme: Property<UriScheme> = project.objects.property(UriScheme::class.java)

    @Input
    @Optional
    fun getUserName(): String? = userName.orNull

    fun setUserName(name: Provider<String>) = this.userName.set(name)

    @Input
    @Optional
    fun getPassword(): String? = password.orNull

    fun setPassword(name: Provider<String>) = this.password.set(name)

    @Input
    @Optional
    fun getAdminUserName(): String? = adminUserName.orNull

    fun setAdminUserName(name: Provider<String>) = this.adminUserName.set(name)

    @Input
    @Optional
    fun getAdminPassword(): String? = adminPassword.orNull

    fun setAdminPassword(name: Provider<String>) = this.adminPassword.set(name)

    @Input
    fun getServerName(): String = serverName.getOrElse("localhost")

    fun setServerName(name: Provider<String>) = this.serverName.set(name)

    @Input
    @Optional
    fun getGraphName(): String? = graphName.orNull

    fun setGraphName(graph: Provider<String>) = this.graphName.set(graph)

    @Input
    fun getRestPort(): String = restPort.getOrElse(rest_pp_port)

    fun setRestPort(port: Provider<String>) = this.restPort.set(port)

    @Input
    @Optional
    fun getGsqlClientVersion(): String? = gsqlClientVersion.orNull

    fun setGsqlClientVersion(version: Provider<String>) = this.gsqlClientVersion.set(version)

    @Input
    @Optional
    fun getCaCert(): String? = caCert.orNull

    fun setCaCert(cert: Provider<String>) = this.caCert.set(cert)

    @Input
    @Optional
    fun getLogDir(): String? = logDir.orNull

    fun setLogDir(logdir: Provider<String>) = this.logDir.set(logdir)

    @Input
    fun getUriScheme() = uriScheme.get()

    fun setUriScheme(scheme: Provider<UriScheme>) = this.uriScheme.set(scheme)
}

data class ConnectDataSerializable(
    @get:Input val userName: String,
    @get:Input val password: String,
    @get:Input val adminUserName: String?,
    @get:Input val adminPassword: String?,
    @get:Input val serverName: String,
    @get:Input val graphName: String,
    @get:Input val gsqlClientVersion: String?,
    @get:Input val caCert: String?,
    @get:Input val logDir: String?,
    @get:Input val uriScheme: UriScheme?
) : Serializable
