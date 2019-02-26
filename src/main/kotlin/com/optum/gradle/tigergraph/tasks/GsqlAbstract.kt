package com.optum.gradle.tigergraph.tasks

import com.optum.gradle.tigergraph.data.ConnectionData
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Nested

abstract class GsqlAbstract : JavaExec() {
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
