# Tasks and Types
This plugin defines several tasks and a task type.

## Tasks

### gsqlCopySources
This is a task type that copies gsql files from your
[`scriptDir`](gsql_task.md#scriptDir) and copies them to your project's build
directory.

Generally you don't need to worry about this task type or it's associated
`gsqlCopySources` task that get's added to your project with the plugin.
However, for the curious I'll explain what it does here.

This task calls the project's [copy][1] task. It uses the directory defined
by setting [`scriptDir`](configuration.md#scriptDir) directory as it's `from`
and takes any [`tokens`](configuration.md#tokens) to perform any replacements
within your source files.

All tasks that use the [`GsqlTask`](#gsqlTask) type automatically
[dependsOn][2] this task.

### gsqlDeleteToken
This task deletes the authentication token created by [`gsqlToken`](#gsqlToken).

### gsqlNewProject
This task allows you to create a new project interactively. This  task guides
you through the process of setting up a gradle Tigergraph project, and creates
a skeleton structure for your project.

### gsqlShell
This task type and it's task, `gsqlShell` allows a user to run the gsql client
interactively.  This represents a huge convenience when working with a remote
Tigergraph server.

This task also uses the [`tigergraph`](configuration.md) closure. This means
you can connect to your Tigergraph server with your credentials just by
executing this task.

Gradle's console defaults can be a bit noisy for interactive use. I suggest
using the `--console=plain` option when executing this task. More information
can be found [here][3] and [here][4].

```shell
$ gradle --console=plain gsqlShell
```

This will take the credentials and server you've configured in
[`tigergraph`](configuration.md), and connect you to your server.

This task also defines a command line argument that allows you to use the
[`superUser`](gsql_task.md#superUser) credentials.
```shell
$ gradle --console=plain gsqlShell --super-user
```

?> You can always get more information about a gradle task by executing the `help` task.
`gradle help --task gsqlShell`

### gsqlToken
This task uses [`authSecret`](configuration.md#authSecret) to make a connection
to the Tigergraph REST++ server and obtain a token. This token is then stored
in the plugin's extension as a property. It's accessible to your build script
as `tigergraph.token`. Once your done with your token, you can have it deleted
by using the [`gsqlDeleteToken`](#gsqlDeleteToken) task.

## Types

### GsqlTask
This is the work horse of the plugin. Using this task is how you invoke locally
stored gsql scripts against your remote (or local) Tigergraph server. See
[Configuration](configuration.md#GsqlTask) for all the options.

[1]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:copy(groovy.lang.Closure)
[2]: https://docs.gradle.org/current/dsl/org.gradle.api.Task.html#org.gradle.api.Task:dependsOn
[3]: https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
[4]: https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_logging
