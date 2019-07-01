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

[1]: https://docs.gradle.org/current/userguide/getting_started.html
