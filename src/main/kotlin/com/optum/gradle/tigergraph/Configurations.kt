package com.optum.gradle.tigergraph

internal object Configurations {
    /**
     * The name of the runtime configuration for add Classpath dependencies to the plugin.
     *
     */
    const val gsqlRuntime = "gsqlRuntime"

    /**
     * The name of the extension for configuring the runtime behavior of the plugin.
     *
     * @see com.optum.gradle.tigergraph.GsqlPluginExtension
     */
    const val extensionName = "tigergraph"
}