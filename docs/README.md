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

!> As of version `1.3.0` of this plugin the default gsql_client version move
from `2.3.2.1` to `2.4.0`. This means that by default the older versions of
this plugin won't be able (by default) to connect to newer versions of
Tigergraph, and vice versa. See the
[FAQ](faq/faq.md#how-do-i-set-my-gsql-client-version) for ways to resolve.

# Getting Started

> Add the plugin to your build file

```
kotlin plugins {
    id("com.optum.giraffle") version "1.3.0"
}

repositories {
    jcenter()
}
```

This activates the Giraffle plugin for your build script. It's necessary to add
the `jcenter` repository to your project because the [gsql
client](https://bintray.com/beta/#/tigergraphecosys/tgjars) is hosted there.

This enables the plugin for your build. The plugin defines a handful of tasks
for your project and a couple of task types.

This plugin also defines a simple configuration closure that applies to all the
task types defined by the plugin. We'll use this to apply our server address
and user credentials for connecting to Tigergraph.

[1]: https://docs.gradle.org/current/userguide/getting_started.html
