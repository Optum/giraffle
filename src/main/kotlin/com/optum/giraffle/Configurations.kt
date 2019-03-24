package com.optum.giraffle

internal object Configurations {
    /**
     * The name of the runtime configuration for add Classpath dependencies to the plugin.
     *
     */
    const val gsqlRuntime = "gsqlRuntime"

    /**
     * The name of the extension for configuring the runtime behavior of the plugin.
     *
     * @see com.optum.giraffle.GsqlPluginExtension
     */
    const val extensionName = "tigergraph"

    /**
     * The location for script directory defaults
     */
    const val scriptDirectoryName = "db_scripts"
}
