package com.optum.gradle.tigergraph.data

import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Optional

class ConnectionData(project: Project) {
    private val userName: Property<String> = project.objects.property(String::class.java)
    private val adminUserName: Property<String> = project.objects.property(String::class.java)
    private val password: Property<String> = project.objects.property(String::class.java)
    private val adminPassword: Property<String> = project.objects.property(String::class.java)

    @Input
    fun getUserName(): String {
        return userName.toString()
    }

    fun setUserName(name: Provider<String>) {
        this.userName.set(name)
    }

    @Input
    fun getPassword(): String {
        return password.toString()
    }

    fun setPassword(name: Provider<String>) {
        this.password.set(name)
    }

    @Input
    @Optional
    fun getAdminUserName(): String? {
        return adminPassword.orNull.toString()
    }

    fun setAdminUserName(name: Provider<String>) {
        this.adminUserName.set(name)
    }

    @Input
    @Optional
    fun getAdminPassword(): String? {
        return password.orNull.toString()
    }

    fun setAdminPassword(name: Provider<String>) {
        this.password.set(name)
    }

}