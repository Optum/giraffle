package com.optum.giraffle

import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

open class GsqlPluginExtension(project: Project) {
    /**
     * Returns the directory for gsql script sources.
     *
     * @return The input script directory
     */
    val scriptDir: DirectoryProperty = project.objects.directoryProperty()

    /**
     * Returns the directory for gsql script output.
     *
     * @return The output script directory
     */
    val outputDir: DirectoryProperty = project.objects.directoryProperty()

    /**
     * Returns the tokens intended to be used in the Ant style filter.
     *
     * @return The map for token replacement
     */
    val tokens: MapProperty<String, String> = project.objects.mapProperty(String::class.java, String::class.java)

    /**
     * Returns the server name for the giraffle server.
     *
     * @return The giraffle server
     */
    val serverName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the non-privileged user to use for connecting to giraffle
     *
     * @return The user for connecting to giraffle
     */
    val userName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the password associated with the non-privileged user
     *
     * @return The password for userName
     */
    val password: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the privileged user to use for connecting to giraffle
     *
     * @return The user for connecting to giraffle
     */
    val adminUserName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the password associated with the non-privileged user
     *
     * @return The password for userName
     */
    val adminPassword: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the graphname to apply as context for gsql
     *
     * @return The graphname
     */
    val graphName: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the secret used to generate the OAUTH style token for Tigergraph REST endpoint
     *
     * @return The secret for creating the token
     */
    val authSecret: Property<String> = project.objects.property((String::class.java))

    /**
     * Returns the token used by the Tigergraph REST++ endpoint
     *
     * @return The Token
     */
    val token: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the port to use for connecting to the REST++ server
     *
     * @return the rest port
     */
    val restPort: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the the port to use for the UI/gsql server
     *
     * @return the gsql port
     */
    val gsqlPort: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the gsql client version to use
     *
     * @return the gsql client version
     */
    val gsqlClientVersion: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the cacert path to use
     *
     * @return the cacert path
     */
    val caCert: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the logdir path to use
     *
     * @return the logdir path
     */
    val logDir: Property<String> = project.objects.property(String::class.java)

    /**
     * Returns the uriScheme used by the Tigergraph server
     *
     * @return the uriScheme
     */
    val uriScheme: Property<UriScheme> = project.objects.property(UriScheme::class.java)
}
