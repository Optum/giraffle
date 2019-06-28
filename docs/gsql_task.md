# GsqlTask Type
This is the main type you'll use within this plugin. It pulls all it's
configuration from [tigergraph][1] closure.

Define tasks of this type to execute your gsql scripts. Easiest way to use this
is to import the class at the top of your build file.

```gradle
import com.optum.giraffle.tasks.GsqlTask
```

## Properties
The tasks are controlled by these properties. Either
[`scriptCommand`](#scriptCommand) or [`scriptPath`](#scriptPath) are the only
required tasks.

Although not defined by this task, best practice would also involve adding a
[group][2] and a [description][3].

### graphName
Use this value to set the sub-graph context for this script task. This tasks
overrides the [`graphName`](configuration.md#graphName) property set by the
`tigergraph` closure.

> String

### scriptCommand
Use this value to execute a command on the Tigergraph server. Useful for one
line commands.

> String

### scriptPath
Use this value to set the path, relative to the
[`scriptDir`](configuration.md#scriptDir), of the script to execute.

> String

### superUser
Use this value to have this tasks executing with the
[`adminUserName`](configuration.md#adminUserName) credentials.

> Boolean

### useGlobal
Use this value to have the script executed in the graph's global context when
you've used [`graphName`](configuration.md#graphName) set the default context
to a sub-graph.

> Boolean

[1]: configuration.md
[2]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:group
[3]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:description
