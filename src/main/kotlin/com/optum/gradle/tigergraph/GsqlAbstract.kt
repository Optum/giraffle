package com.optum.gradle.tigergraph

import org.gradle.api.tasks.JavaExec
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

abstract class GsqlAbstract : JavaExec() {
    @Input
    protected val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

    /*
    @Input
    private val adminUserName: String? = extension.adminUserName

    @Input
    private val userName: String? = extension.userName

    @Input
    private val adminPassword: String? = extension.adminPassword

    @Input
    private val password: String? = extension.password
    */

    @get:Nested
    val adminUserName = extension.adminUserName

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
                    adminUserName,
                    adminPassword,
                    "Admin username and password needs to be provided.")

    private fun getNonPrivCredentials(): List<String> =
            getCredentials(
                    userName,
                    password,
                    "Username and password need to be provided.")

    private fun getCredentials(usernameProperty: String?, passwordProperty: String?, message: String): List<String> {
        if (usernameProperty == null || passwordProperty == null) throw GradleException(message)
        return listOf("-u", usernameProperty, "-p", passwordProperty)
    }
}
