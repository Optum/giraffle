package com.optum.gradle.tigergraph

open class GsqlPluginExtension {
    val scriptPath: String = "db_scripts"
    val tokens: Map<String, String> = emptyMap<String, String>()
    val serverName: String = "localhost"
    val userName: String = "tigergraph"
    val password: String = "tigergraph"
    val adminUserName: String? = null
    val adminPassword: String? = null
}
