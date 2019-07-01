# GsqlCopySources
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

All tasks that use the [`GsqlTask`](gsql_task.md) type automatically
[dependsOn][2] this task.

# GsqlShell
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

# New Project
This type / task allows you to create a new project interactively. It's use is
experimental at this time, and more documentation will follow once it's more
stable.

[1]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:copy(groovy.lang.Closure)
[2]: https://docs.gradle.org/current/dsl/org.gradle.api.Task.html#org.gradle.api.Task:dependsOn
[3]: https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
[4]: https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_logging
