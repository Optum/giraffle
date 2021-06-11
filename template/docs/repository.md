# Repository

Gradle needs to know where to go to obtain the gsql\_client. It is currently
hosted on github's maven repository. This repository requires authentication in
order to use it.

Gradle's repository section needs to look something like this:

```kotlin
repository {
    maven {
       url = uri("https://maven.pkg.github.com/tigergraph/gsql_client")
       credentials {
          username=project.findProperty("gpr.user") as String
          password=project.findProperty("gpr.key") as String
       }
    }
}
```

The key here is to give gradle access to your credentials for GitHub. My
preference is to place these values in my `~/.gradle/gradle.properties`. This
way all my projects have access to these values. You may use any one of
gradle's [many
facilities](https://docs.gradle.org/current/userguide/build_environment.html)
for injecting properties into a project.

```jproperties
gpr.user=jmeekhof
gpr.key=ghp_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

