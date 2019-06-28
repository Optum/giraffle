# GsqlShell
This task type and it's task, `gsqlShell` allows a user to run the gsql client
interactively.  This represents a huge convenience when working with a remote
Tigergraph server.

This task also uses the [`tigergraph`](configuration.md) closure. This means
you can connect to your Tigergraph server with your credentials just by
executing this task.

Gradle's console defaults can be a bit noisy for interactive use. I suggest
using the `--console=plain` option when executing this task. More information
can be found [here][1] and [here][2].

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

[1]: https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_configuration_properties
[2]: https://docs.gradle.org/current/userguide/command_line_interface.html#sec:command_line_logging

