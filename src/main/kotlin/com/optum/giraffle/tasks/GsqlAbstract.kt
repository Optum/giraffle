package com.optum.giraffle.tasks

import com.optum.giraffle.Configurations.gsqlRuntime
import com.optum.giraffle.GsqlPluginExtension
import com.optum.giraffle.data.ConnectionData
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.options.Option

abstract class GsqlAbstract : JavaExec() {

    @Internal
    val connectionData: ConnectionData = ConnectionData(project)

    @Internal
    var superUser: Boolean = false
    @Option(option = "super-user", description = "Set the super-user option for the task(s) being run.")
    set

    @Internal
    protected val gsqlPluginExtension: GsqlPluginExtension

    @Internal
    public var graphStudio: Boolean = false

    init {
        val cfg: Configuration? = project.configurations.findByName(gsqlRuntime)
        gsqlPluginExtension = project.extensions.getByType(GsqlPluginExtension::class.java)

        cfg?.let { classpath = cfg }

        connectionData.setAdminUserName(gsqlPluginExtension.adminUserName)
        connectionData.setAdminPassword(gsqlPluginExtension.adminPassword)
        connectionData.setUserName(gsqlPluginExtension.userName)
        connectionData.setPassword(gsqlPluginExtension.password)
        connectionData.setServerName(gsqlPluginExtension.serverName)
        connectionData.setGraphName(gsqlPluginExtension.graphName)
        connectionData.setGsqlClientVersion(gsqlPluginExtension.gsqlClientVersion)
        connectionData.setCaCert(gsqlPluginExtension.caCert)
        connectionData.setLogDir(gsqlPluginExtension.logDir)

        connectionData.getGsqlClientVersion()?.let {
            environment("GSQL_CLIENT_VERSION", it)
        }

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
        val list: MutableList<String> = mutableListOf()

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

    protected fun getGraphStudio(): List<String> {
        val list: MutableList<String> = mutableListOf()
        if (graphStudio) {
            list.add("-graphstudio")
        }
        return list
    }

    @Internal
    protected fun getCaCert(): List<String> {
        val list: MutableList<String> = mutableListOf()
        val caCert: String? = connectionData.getCaCert()

        caCert?.let {
            list.add("--cacert")
            list.add(it)
        }
        return list
    }

    @Internal
    protected fun getLogDir(): List<String> {
        val list: MutableList<String> = mutableListOf()
        val logDir: String? = connectionData.getLogDir()

        logDir?.let {
            list.add("--logdir")
            list.add(it)
        }
        return list
    }
}
