# Tigergraph configuration closure
This plugin defines a closure that contains all the configuration for
connecting to your Tigergraph server. This configuration closure applies to all
tasks and task types that this plugin defines.

```kotlin
tigergraph {
    scriptDir.set(file("db_scripts"))
    // We wouldn't actually hard code any of these values, we'd use properties
    userName.set("joe")
    password.set("s3cret")
    graphName.set("myGraph")
}
```

## Properties
These are the properties that may be set within the `tigergraph` closure.

### adminPassword
Set this property to the value of the password to use in conjunction with the
[`adminUserName`](#adminUserName).

> Type: String

### adminUserName
Set this property to the super-user for Tigergraph.

> Type: String

### graphName
Set the default graph context for Tigergraph.

> Type: String

### password
Set this property to the value of the password to use in conjunction with the
[`userName`](#userName).

> Type: String

### scriptDir
Set this to the path that holds your gsql scripts. All
[`scriptPath`](#scriptPath) arguments are interpreted relative to this path.

> Type: File

> Default value: file("db_scripts")

### tokens
Set this property to a map which defines how the plugin should perform token
replacement within your source scripts. Internally this plugin uses an [ant
filter][1].

> Type: Map<String, String>

### userName
Set this property to the standard user for Tigergraph.

> Type: String

[1]: https://ant.apache.org/manual/api/org/apache/tools/ant/filters/ReplaceTokens.html
