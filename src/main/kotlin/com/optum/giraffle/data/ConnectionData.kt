package com.optum.giraffle.data

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Optional
import java.io.Serializable

class ConnectionData(project: Project) {
    private val userName: Property<String> = project.objects.property(String::class.java)
    private val adminUserName: Property<String> = project.objects.property(String::class.java)
    private val password: Property<String> = project.objects.property(String::class.java)
    private val adminPassword: Property<String> = project.objects.property(String::class.java)
    private val serverName: Property<String> = project.objects.property(String::class.java)

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
}

data class ConnectDataSerializable(
    @get:Input val userName: String,
    @get:Input val password: String,
    @get:Input val adminUserName: String?,
    @get:Input val adminPassword: String?,
    @get:Input val serverName: String
) : Serializable
