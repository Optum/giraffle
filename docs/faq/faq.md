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
# How do I set my load path dynamically per environment?
Use the [token replacement][2] feature in conjunction with the [Properties][1]
plugin.

Consider a build file that looks something like:

build.gradle.kts
```kotlin
import com.optum.giraffle.tasks.GsqlTask

plugins {
    id("com.optum.giraffle") version "1.3.0"
    id("net.saliman.properties") version "1.5.1"
}

val gsqlGraphname: String by project
val gsqlSysDataRoot: String by project

val tokenMap: LinkedHashMap<String, String> =
    linkedMapOf(
        "graphname" to gsqlGraphname,
        "sys_dataroot" to gsqlSysDataRoot
    )

tigergraph {
    tokens.set(tokenMap)
}
```

You'd have the rest of the `tigergraph` section filled out just like the
[example](basic_example.md) demonstrates.

Then put the token in your create loading job scripts.

load_people.gsql
```gsql
SET sys.data_root = "@sys_dataroot@"

CREATE LOADING JOB load_people FOR GRAPH @graphname@ {
DEFINE FILENAME f = "$sys.data_root/people.csv";
LOAD f
  TO VERTEX People VALUES ($0, $1, $2);
}
```
Using this method you can change your load per environment simply by changing
the `gsqlSysDataRoot` property.

[1]: https://github.com/stevesaliman/gradle-properties-plugin
[2]: ../configuration.md#tokens
