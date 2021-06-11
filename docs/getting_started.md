# Giraffle

> A Gradle plugin for Tigergraph

Giraffle can be used to help you automate the deployment of schema, loading
jobs, and queries to Tigergraph.

Use of this plugin presumes some prior knowledge of Gradle and using it to
build software. I'd suggest [Getting Started with Gradle][1] if you are new to
Gradle.

Using Giraffle helps you keep your code local while executing your gsql scripts
on a remote server. Giraffle also helps you keep environment specific
configuration out of your code. Most importantly it helps you keep your
credentials out of your code.

!> Version `1.3.5` changes the default gsql\_client to `3.1.2`. This client
is backwards compatible with previous versions. With `jcenter` shutting down it
will be necessary to obtain the gsql\_client from a different repository. See
[Repository](repository.md) for instructions on how to setup the repository
section.

!> Version `1.3.2.1` changes the default gsql\_client to `2.5.2`. This client
is backwards compatible with versions `v2_5_2`, `v2_5_0`, `v2_4_1`, `v2_4_0`,
and `v2_3_2`.  See [Configuration](configuration.md#gsqlclientversion) for more
details.

!> As of version `1.3.0` of this plugin the default gsql\_client version move
from `2.3.2.1` to `2.4.0`. This means that by default the older versions of
this plugin won't be able (by default) to connect to newer versions of
Tigergraph, and vice versa. See the
[FAQ](faq/faq.md#how-do-i-set-my-gsql-client-version) for ways to resolve.

# Getting Started
!> Must have Gradle installed. Must have build file. Use `gradle init` to build
out initial gradle framework.

> Add the plugin to your build file

```kotlin
plugins {
    id("com.optum.giraffle") version "1.3.5"
}

repositories {
    mavenCentral()
    maven {
       url = uri("https://maven.pkg.github.com/tigergraph/gsql_client")
       credentials {
          username=project.findProperty("gpr.user") as String
          password=project.findProperty("gpr.key") as String
       }
    }
}
```

This activates the Giraffle plugin for your build script. It's necessary to add
the github maven repository to your project because the [gsql
client](https://github.com/orgs/tigergraph/packages?repo_name=gsql_client) is
hosted there. You'll need to provide user credentials to access this
repository. Details can be found [here](repository.md).

This enables the plugin for your build. The plugin defines a handful of tasks
for your project and a couple of task types.

This plugin also defines a simple configuration closure that applies to all the
task types defined by the plugin. We'll use this to apply our server address
and user credentials for connecting to Tigergraph.

[1]: https://docs.gradle.org/current/userguide/getting_started.html
