# Tigergraph Gradle Plugin
This plugin creates a dsl for Tigergraph. The dsl allows you to describe
connections to a Tigergraph server.

## Status
The Tigergraph plugin is still under development. To use this plugin, you'll
need to publish it to your local maven repository. This is typically is
`~/.m2/repository`. Soon we'll publish this plugin to an Artifactory
repository.

## Development Assistance
Fork and clone this repository. After this you should be able to make changes,
compile, test and publish.  The `publishToMavenLocal` will make the plugin
available to your Gradle projects.

## Using the plugin
This plugin requires a jar file that currently needs to be obtained from your
Tigergraph installation. Once you've obtained the plugin, I find it handy to
publish it to your local Maven repository.

The jar you'll need is the `gsql_client.jar`. Instructions to find it can be
found at [Tigergraph's documentation site](tds)

After obtaining the jar, use Maven's [install plugin](mip) to install it in
your local repository.

Example:
```sh
mvn install:install-file \
    -Dfile=./gsql_client.jar \
    -DgroupId=com.tigergraph.client \
    -DartifactId=Driver \
    -Dversion=2.1.7 \
    -Dpackaging=jar
```

At this point you should have 2 jars installed locally. With these installed
you can create your gradle build file that uses the plugin.

init.gradle.kts
```
initscript {
    repositories {
        mavenLocal()
    }
    dependencies {
        classpath("com.optum.gradle.tigergraph:tigergraph-gradle-plugin:0.1.0")
    }
}
```

build.gradle.kts
```kts
plugins {
    id("com.optum.gradle.tigergraph) version "0.1.0"
}

val tigergraph by configurations.creating
dependencies {
    tigergraph("com.tigergraph.client:Driver:2.1.7")
}

tigergraph {
    scriptDir = ""
    tokens = ""
    serverName = ""
    userName = ""
    password = ""
    adminUserName = ""
    adminPassword = ""
}
```

This should put everything in order. The plugin defines a couple of tasks, and
a dsl to handle connection parameters to your Tigergraph server(s).







[tds](https://docs.tigergraph.com/dev/using-a-remote-gsql-client)
[mip](https://maven.apache.org/plugins/maven-install-plugin/examples/specific-local-repo.html)
