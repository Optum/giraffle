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

    /**
     *  The default gsql_client version
     */
    const val gsql_client_version = "2.3.2.1"

    /**
     * The version of net.saliman.properties to use in templates
     */
    const val net_saliman_properties_version = "1.5.1"
}
