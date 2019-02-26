package com.optum.gradle.tigergraph.tasks

import com.optum.gradle.tigergraph.GsqlPluginExtension
import com.optum.gradle.tigergraph.data.ConnectionData
import org.gradle.api.tasks.JavaExec
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

abstract class GsqlAbstract : JavaExec() {
    @Input
    protected val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

    /*
    @get:Nested
    val connectionDataS: Property<ConnectDataSerializable> = project.objects.property(ConnectDataSerializable::class.java)
    */

    @get:Nested
    val connectionData: ConnectionData = ConnectionData(project)

    @Input
    var superUser: Boolean = false

    abstract fun buildArgs(): List<String>

    protected fun determineUser(superUser: Boolean): List<String> =
            when (superUser) {
                true -> getAdminCredentials()
                false -> getNonPrivCredentials()
            }

    private fun getAdminCredentials(): List<String> =
            getCredentials(
                    connectionData.getAdminUserName(),
                    connectionData.getAdminPassword(),
                    "Admin username and password needs to be provided.")

    private fun getNonPrivCredentials(): List<String> =
            getCredentials(
                    connectionData.getUserName(),
                    connectionData.getPassword(),
                    "Username and password need to be provided.")

    private fun getCredentials(usernameProperty: String?, passwordProperty: String?, message: String): List<String> {
        if (usernameProperty == null || passwordProperty == null) throw GradleException(message)
        return listOf("-u", usernameProperty, "-p", passwordProperty)
    }
}
