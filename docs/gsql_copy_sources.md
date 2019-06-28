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

[1]: https://docs.gradle.org/current/dsl/org.gradle.api.Project.html#org.gradle.api.Project:copy(groovy.lang.Closure)
[2]: https://docs.gradle.org/current/dsl/org.gradle.api.Task.html#org.gradle.api.Task:dependsOn
