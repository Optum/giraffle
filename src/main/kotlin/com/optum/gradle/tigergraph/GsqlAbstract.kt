package com.optum.gradle.tigergraph

import org.gradle.api.tasks.JavaExec
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input

abstract class GsqlAbstract : JavaExec() {
    @Input
    protected val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

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
                    extension.adminUserName,
                    extension.adminPassword,
                    "Admin username and password needs to be provided.")

    private fun getNonPrivCredentials(): List<String> =
            getCredentials(
                    extension.userName,
                    extension.password,
                    "Username and password need to be provided.")

    private fun getCredentials(usernameProperty: String?, passwordProperty: String?, message: String): List<String> {
        if (usernameProperty == null || passwordProperty == null) throw GradleException(message)
        return listOf("-u", usernameProperty, "-p", passwordProperty)
    }

    protected fun getAdminUsername(): String? = extension.adminUserName

    protected fun getAdminPassword(): String? = extension.password

    protected fun getUsername(): String? = extension.userName

    protected fun getPassword(): String? = extension.password
}