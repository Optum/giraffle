package com.optum.gradle.tigergraph

import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

open class GsqlTask() : JavaExec() {
    private val extension: GsqlPluginExtension = project.extensions.findByName("tigergraph") as GsqlPluginExtension

    @Input
    var scriptPath: String? = null
    @Input
    var superUser: Boolean = false

    @TaskAction
    override fun exec() {

        val cfg: Configuration? = project.configurations.findByName("tigergraph")

        if (cfg != null) {
            classpath = cfg
        }

        main = "org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader"
        args = buildArgs()

        args.add("${project.buildDir}/$scriptPath")
    }

    private fun buildArgs(): List<String> {
        val newArgs: MutableList<String> = mutableListOf<String>()

        newArgs.add("--ip")
        newArgs.add(extension.serverName)
        newArgs += determineUser(superUser)

        newArgs.add(scriptPath!!)

        return newArgs
    }

    private fun determineUser(superUser: Boolean): List<String> =
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

    private fun getAdminUsername(): String? = extension.adminUserName

    private fun getAdminPassword(): String? = extension.password

    private fun getUsername(): String? = extension.userName

    private fun getPassword(): String? = extension.password
}
