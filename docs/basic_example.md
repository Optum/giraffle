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
    id("com.optum.giraffle") version "1.3.3"
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

# HTTP Example
This example demonstrates how you can use the GsqlTokenTask and http-build-ng
plugin to connect to Tigergraph's REST++ endpoint. This can be useful anytime
you want to automate small loads via the REST++ interface. Here I'm using the
[gradle-http-plugin][2], which is a wrapper for [http-builder-ng][3].

```kotlin
import com.optum.giraffle.tasks.*
import com.optum.giraffle.*
import io.github.httpbuilderng.http.HttpTask // <1>

buildscript {
    this.dependencies{
        this.classpath("com.opencsv:opencsv:3.8")
    }
}

plugins {
    id("com.optum.giraffle") version "1.3.3"
    id("net.saliman.properties") version "1.5.1"
    id("io.github.http-builder-ng.http-plugin") version "0.1.1"
}

repositories {
    jcenter()
}

http { // <2>
    config{
        it.request.setUri("${gHostUriType}://${gHost}:${gRestPort}")
        it.request.headers["Authorization"] = "Bearer ${tigergraph.token.get()}" // <2.1>
    }
}

val gAdminPassword: String by project
val gAdminUserName: String by project
val gCertPath: String? by project
val gClientVersion: String? by project // <3>
val gGraphName: String by project
val gHost: String by project
val gHostUriType: String by project
val gPassword: String by project
val gRestPort: String by project
val gSecret: String? by project
val gUserName: String by project

val tokenMap: LinkedHashMap<String, String> = linkedMapOf("graphname" to gGraphName)

val schemaGroup: String = "Schema Tasks"
val loadingGroup: String = "Loading Tasks"

tigergraph {
    adminPassword.set(gAdminPassword)
    adminUserName.set(gAdminUserName)
    graphName.set(gGraphName)
    password.set(gPassword)
    scriptDir.set(file("db_scripts"))
    serverName.set(gHost)
    tokens.set(tokenMap)
    uriScheme.set(UriScheme.HTTPS)
    userName.set(gUserName)
    gClientVersion?.let { // <4>
        gsqlClientVersion.set(it)
    }
    gCertPath?.let {
        caCert.set(it)
    }
    gSecret?.let {
        authSecret.set(it)
    }
    logDir.set(file("./logs"))
}

tasks {
    wrapper {
        gradleVersion = "6.0.1"
    }

    register<GsqlTask>("showSchema") {
        scriptCommand = "ls"
        group = schemaGroup
        description = "Run simple `ls` command to display vertices, edges, and jobs for current graph"
    }

    register<GsqlTask>("createSchema") {
        scriptPath = "schema/create.gsql"
        useGlobal = true
        group = schemaGroup
        description = "Runs gsql to create a schema"
    }

    register<GsqlTask>("dropSchema") {
        scriptPath = "schema/drop.gsql"
        group = schemaGroup
        description = "Runs gsql to drop the database schema"
    }

    register<GsqlTask>("createLoadPartType") {
        scriptPath = "load/create/load_part_type.gsql"
        group = loadingGroup
        description = "Creates loading job for loading part types"
    }

    register<HttpTask>("loadPartType") { // <5>
        description = "Load data via the REST++ endpoint"
        post { httpConfig ->
            httpConfig.request.uri.setPath("/ddl/${gGraphName}")
            httpConfig.request.uri.setQuery(
                    mapOf(
                            "tag" to "loadPartType",
                            "filename" to "f1",
                            "sep" to ",",
                            "eol" to "\n"
                    )
            )
            httpConfig.request.setContentType("text/csv")
            val stream = File("data.csv").inputStream()
            httpConfig.request.setBody(stream)
        }

    }

    val getToken by registering(GsqlTokenTask::class){ }

    register<GsqlTokenDeleteTask>("deleteToken") { }

    register<HttpTask>("getVersion") {
        description = "Get the server version from Tigergraph"
        get {
            it.request.uri.setPath("/version")
            it.response.success { fs, x ->
                println(fs )
                println(x)
                println("Success")
            }
        }
    }

    withType<HttpTask>().configureEach { // <6>
        dependsOn(getToken)
    }
}
```
<1> This is a gradle plugin that provides an easy to use HTTP interface.

<2> This is where you configure the defaults for the httpbuilderng interface.

<2.1> This is how you authenticate yourself to the Tigergraph REST++ endpoint.

<3> `?` is how you specify a nullable, and therefore optional property.

<4> `?.let {}` is how you safely use a nullable property.

<5> `HttpTask` is a task provided by httpbuilderng.

<6> This ensures that there's a token available to the http interface prior to
making any calls to REST++ endpoint.

[1]: https://github.com/stevesaliman/gradle-properties-plugin

[2]: https://github.com/http-builder-ng/gradle-http-plugin

[3]: https://http-builder-ng.github.io/http-builder-ng
