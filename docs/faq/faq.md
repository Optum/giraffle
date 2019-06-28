# How do I set my gsql client version?
By default this plugin looks a configuration named `gqslRuntime`. As of version
`1.2.0` the plugin has a default setting to use version `2.3.2.1` of the gsql
client. Later versions will use the later release of `2.4.0`. To change the
client version you're use add the following to your build file.

Running version 1.2.0, and connecting to Tigergraph 2.4 and later:
```kotlin
dependencies {
    gsqlRuntime("com.tigergraph.client:gsql_client:2.4.0")
}
```

Running version 1.3.0, and connecting to Tigergraph 2.3 and earlier:
```kotlin
dependencies {
    gsqlRuntime("com.tigergraph.client:gsql_client:2.3.2.1")
}
```
