package com.optum.gradle.tigergraph

open class GsqlPluginExtension {
    var scriptDir: String = "db_scripts"
    var tokens: Map<String, String> = emptyMap<String, String>()
    var serverName: String = "localhost"
    var userName: String = "tigergraph"
    var password: String = "tigergraph"
    var adminUserName: String? = null
    var adminPassword: String? = null
}
