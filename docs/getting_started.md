# Getting Started

> Add the plugin to your build file

```kotlin
plugins {
    id("com.optum.giraffle") version "1.2.0"
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
