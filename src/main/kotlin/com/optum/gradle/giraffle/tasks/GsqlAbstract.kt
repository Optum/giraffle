package com.optum.gradle.giraffle.tasks

import com.optum.gradle.giraffle.Configurations.extensionName
import com.optum.gradle.giraffle.Configurations.gsqlRuntime
import com.optum.gradle.giraffle.GsqlPluginExtension
import com.optum.gradle.giraffle.data.ConnectionData
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested

abstract class GsqlAbstract() : JavaExec() {

    @get:Nested
    val connectionData: ConnectionData = ConnectionData(project)

    @Input
    var superUser: Boolean = false

    @Internal
    protected val gsqlPluginExtension: GsqlPluginExtension

    init {
        val cfg: Configuration? = project.configurations.findByName(gsqlRuntime)
        this.gsqlPluginExtension = project.extensions.getByType(GsqlPluginExtension::class.java).also {
            project.extensions.getByName(extensionName)
        }

        cfg?.let { classpath = cfg }

        this.connectionData.setAdminUserName(gsqlPluginExtension.adminUserName)
        this.connectionData.setAdminPassword(gsqlPluginExtension.adminPassword)
        this.connectionData.setUserName(gsqlPluginExtension.userName)
        this.connectionData.setPassword(gsqlPluginExtension.password)
        this.connectionData.setServerName(gsqlPluginExtension.serverName)

        main = "com.tigergraph.client.Driver"
    }

    abstract fun buildArgs(): List<String>

    protected fun determineUser(superUser: Boolean): List<String> =
            when (superUser) {
                true -> getAdminCredentials()
                false -> getNonPrivCredentials()
            }

    private fun getAdminCredentials(): List<String> =
            getCredentials(
                    connectionData.getAdminUserName(),
                    connectionData.getAdminPassword())

    private fun getNonPrivCredentials(): List<String> =
            getCredentials(
                    connectionData.getUserName(),
                    connectionData.getPassword())

    private fun getCredentials(usernameProperty: String?, passwordProperty: String?): List<String> {
        val list: MutableList<String> = mutableListOf<String>()

        usernameProperty?.let {
            list.add("-u")
            list.add(it)
        }

        passwordProperty?.let {
            list.add("-p")
            list.add(it)
        }
        return list
    }
}
