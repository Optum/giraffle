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

These are the properties that may be set within the `tigergraph` closure.

## adminPassword
Set this property to the value of the password to use in conjunction with the
[`adminUserName`](#adminUserName).

> Type: String

## adminUserName
Set this property to the super-user for Tigergraph.

> Type: String

## authSecret
Set this to the value of the secret created in Tigergraph. See [Creating
Secrets][5] and [REST++ Authentication][6] in Tigergraph's documentation.

> Type: String

## caCert
Set the path to the certificate file for connecting to the Tigergraph server.

This certificate can be obtained with the following command:

```
openssl s_client -connect <tigergraphserver>:14240 < /dev/null 2> /dev/null | \
openssl x509 -text > cert.txt
```

This connects to your tigergraph server, obtains the public key, formats it in
an acceptable format to use, and stores it in `cert.txt`.

> Type: String

## graphName
Set the default graph context for Tigergraph.

> Type: String

## gsqlClientVersion
Set this property to specify the Tigergraph server version you will be connecting to.

As of version 2.5.2 the supported version strings are:

* `v2_5_2`
* `v2_5_0`
* `v2_4_1`
* `v2_4_0`
* `v2_3_2`

> Type: String

## logDir
Set the directory for the gsql client to put it's logs into.

> Type: File

## password
Set this property to the value of the password to use in conjunction with the
[`userName`](#userName).

> Type: String

## scriptDir
Set this to the path that holds your gsql scripts. All
[`scriptPath`](#scriptPath) arguments are interpreted relative to this path.

> Type: File<br/>Default value: file("db_scripts")

## token
This property is used to hold the OAUTH token from Tigergraph's REST++ server.
There are setters for this property, however it's best to use the internal
[`GsqlTokenTask`](#gsqlTokenTask) to manage this for you.

>Type: String

## tokens
Set this property to a map which defines how the plugin should perform token
replacement within your source scripts. Internally this plugin uses an [ant
filter][1].

> Type: Map<String, String>

## uriScheme
Set this property to indicate whether to use http or https for the Tigergraph
REST server. This property is an enumerated type. Your type here is
`com.optum.giraffle.UriScheme`.  Choose either HTTP or HTTPS.

```
import com.optum.giraffle.UriScheme

tigergraph {
    UriScheme.set(UriScheme.HTTPS)
}
```

## userName
Set this property to the standard user for Tigergraph.

> Type: String

# GsqlTask
This is the main type you'll use within this plugin. It pulls all it's
configuration from [tigergraph][2] closure.

Define tasks of this type to execute your gsql scripts. Easiest way to use this
is to import the class at the top of your build file.

```kotlin
import com.optum.giraffle.tasks.GsqlTask

plugins {
    id("com.optum.giraffle") version "1.3.4.1-SNAPSHOT"
}

val createSchema by tasks.registering(GsqlTask::class) {
    scriptPath = "schema.gsql"
    superUser = true
}
```

The tasks are controlled by these properties. Either
[`scriptCommand`](#scriptCommand) or [`scriptPath`](#scriptPath) are the only
required tasks.

Although not defined by this task, best practice would also involve adding a
[group][3] and a [description][4].

## graphName
Use this value to set the sub-graph context for this script task. This tasks
overrides the [`graphName`](configuration.md#graphName) property set by the
`tigergraph` closure.

> Type: String

## graphStudio
I'm not sure what this option does, however it is a CLI option available in the
gsql client.

> Type: Boolean

## scriptCommand
Use this value to execute a command on the Tigergraph server. Useful for one
line commands.

> Type: String

## scriptPath
Use this value to set the path, relative to the
[`scriptDir`](configuration.md#scriptDir), of the script to execute.

> Type: String

## superUser
Use this value to have this tasks executing with the
[`adminUserName`](configuration.md#adminUserName) credentials.

> Type: Boolean

## useGlobal
Use this value to have the script executed in the graph's global context when
you've used [`graphName`](configuration.md#graphName) set the default context
to a sub-graph.

> Type: Boolean

# GsqlTokenDeleteTask
The GsqlTokenDeleteTask is used to remove the token from the Tigergraph server.

# GsqlTokenTask
The GsqlTokenTask is used to get an OAUTH token from Tigergraph to use as
`Bearer` Authorization header.

[1]: https://ant.apache.org/manual/api/org/apache/tools/ant/filters/ReplaceTokens.html
[2]: #tigergraph-configuration-closure
[3]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:group
[4]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:description
[5]: https://docs.tigergraph.com/admin/admin-guide/user-access-management/user-privileges-and-authentication#create-show-drop-secret
[6]: https://docs.tigergraph.com/dev/restpp-api/restpp-requests#rest-authentication
