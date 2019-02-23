package com.optum.gradle.tigergraph

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property

open class GsqlPluginExtension(project: Project) {
    /**
     * Returns the directory for gsql script sources.
     *
     * @return The input script directory
     */
    val scriptDir: DirectoryProperty = project.objects.directoryProperty()

    /**
     * Returns the tokens intended to be used in the Ant style filter.
     *
     * @return The map for token replacement
     */
    val tokens: Map<String, String> = emptyMap<String, String>()

    /**
     * Returns the server name for the tigergraph server.
     *
     * @return The tigergraph server
     */
    val serverName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the non-privileged user to use for connecting to tigergraph
     *
     * @return The user for connecting to tigergraph
     */
    val userName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the password associated with the non-privileged user
     *
     * @return The password for userName
     */
    val password: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the privileged user to use for connecting to tigergraph
     *
     * @return The user for connecting to tigergraph
     */
    val adminUserName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the password associated with the non-privileged user
     *
     * @return The password for userName
     */
    val adminPassword: Property<String> = project.objects.property(String::class.java)
}
