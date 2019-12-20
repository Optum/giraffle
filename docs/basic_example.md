# Basic Example
I like using this with plugin in conjunction with the [Properties][1]
plugin.  This allows you to use and configure different environments. When
using the properties plugin always add an entry to your `.gitignore` for
`gradle-local.properties`. This way you won't commit credentials to your code
repository.

Consider a directory layout as  follows:

```
├── .gitignore
├── build.gradle.kts
├── db_scripts
│   ├── drop.gsql
│   ├── schema.gsql
│   └── show_graph.gsql
├── gradle-local.properties
├── gradle.properties
├── init.gradle.kts
└── settings.gradle.kts
```

build.gradle.kts
```kotlin
import com.optum.giraffle.tasks.GsqlTask

plugins {
    id("com.optum.giraffle") version "1.3.2.1"
    id("net.saliman.properties") version "1.5.1"
}

repositories {
    jcenter()
}

val gsqlGraphname: String by project // <1>
val gsqlHost: String by project
val gsqlUserName: String by project
val gsqlPassword: String by project
val gsqlAdminUserName: String by project
val gsqlAdminPassword: String by project
val tokenMap: LinkedHashMap<String, String> =
    linkedMapOf("graphname" to gsqlGraphname) // <2>

val grpSchema: String = "Tigergraph Schema"

tigergraph { // <3>
    scriptDir.set(file("db_scripts"))
    tokens.set(tokenMap)
    serverName.set(gsqlHost)
    userName.set(gsqlUserName)
    password.set(gsqlPassword)
    adminUserName.set(gsqlAdminUserName)
    adminPassword.set(gsqlAdminPassword)
}

tasks {
    val createSchema by registering(GsqlTask::class) { // <4>
        group = grpSchema
        description = "Create the schema on the database"
        dependsOn("dropSchema") // <5>
        scriptPath = "schema.gsql" // <6>
        superUser = true // <7>
    }

    val dropSchema by registering(GsqlTask::class) {
        group = grpSchema
        description = "Drops the schema on the database"
        scriptPath = "drop.gsql"
        superUser = true
    }
}
```
<1> `by project` is how you references project properties using the Kotlin DSL
for Gradle.

<2> This is how you create a Kotlin map to pass to a property.

<3> Our Tigergraph DSL. These settings apply for all interactions with
Tigergraph.

<4> One way to create a task using the custom task type created by the plugin.

<5> This task will execute _after_ the task that it `dependsOn`.

<6> The path to the source script relative to `scriptDir`.

<7> Informs the plugin which credentials to use.


db_scripts/schema.gsql
```gsql
CREATE VERTEX Person ( primary_id ssn STRING, firstName STRING, lastName STRING)
CREATE UNDIRECTED EDGE FRIENDS (FROM Person, TO Person, effectiveDate DATETIME)

CREATE GRAPH @graphname@(Person, FRIENDS)
```

db_scripts/drop.gsql
```gsql
USE GRAPH @graphname@

DROP GRAPH @graphname@

USE GLOBAL

DROP EDGE FRIENDS
DROP VERTEX Person
```

gradle.properties
```properties
gsqlHost=
gsqlUserName=
gsqlPassword=
gsqlAdminUserName=
gsqlAdminPassword=
gsqlGraphname=hc
```

gradle-local.properties
```properties
gsqlHost=localhost
gsqlUserName=tigergraph
gsqlPassword=tigergraph
gsqlAdminUserName=tigergraph
gsqlAdminPassword=tigergraph
```



[1]: https://github.com/stevesaliman/gradle-properties-plugin
