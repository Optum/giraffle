import org.apache.tools.ant.filters.ReplaceTokens
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration


plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    // id("org.jetbrains.kotlin.jvm") version Version.kotlin
    `java-gradle-plugin`
    `maven-publish`
    signing
    id("com.gradle.plugin-publish") version Version.pluginPublish
    kotlin("jvm") version Version.kotlin
    id("org.jmailen.kotlinter") version Version.kotlinter
    id("net.saliman.properties") version Version.saliman
}

val projectGroup: String by project
val projectVersion: String by project
val projectDescription: String by project

group = projectGroup
version = projectVersion
description = projectDescription

project.ext {
    set("isReleaseVersion", !version.toString().endsWith("SNAPSHOT") )
}

val filterTokens = hashMapOf("project_version" to projectVersion)

val githubUrl = "https://github.com/Optum/${project.name}.git"
val webUrl = githubUrl

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

kotlinter {
    reporters = arrayOf("checkstyle","plain","html")
}

tasks {
    processResources {
        with(project.copySpec {
            from("src/main/resources")
            filter<ReplaceTokens>("tokens" to filterTokens)
        })
    }

    val prepareDocumentationMedia by registering {
        inputs.files(fileTree("template/docs/_media"))
        outputs.dir("$rootDir/docs/_media")
        doFirst {
            copy {
                from("$rootDir/template/docs/_media")
                into("$rootDir/docs/_media")
            }
        }
    }

    val prepareDocumentation by registering {
        inputs.files(fileTree("template/docs"))
        inputs.property("tokens", filterTokens)
        outputs.dir("$rootDir/docs")
        doFirst {
            copy {
                from("$rootDir/template/docs")
                exclude("**/_media/**")
                into("$rootDir/docs")
                filter<ReplaceTokens>("tokens" to filterTokens)
            }
        }
    }

    wrapper {
        gradleVersion = "7.0.2"
    }
}

tasks {
    val cleanPrepareDocumentation by existing
    val prepareDocumentation by existing
    val prepareDocumentationMedia by existing

    prepareDocumentation {
        mustRunAfter(cleanPrepareDocumentation)
    }

    prepareDocumentationMedia {
        mustRunAfter(cleanPrepareDocumentation)
    }

    val prep by registering {
        dependsOn(prepareDocumentation, prepareDocumentationMedia, cleanPrepareDocumentation)
    }

    publishPlugins {
        dependsOn(prep)
    }
}

val integrationTest by sourceSets.creating

val integrationTestTask = tasks.register<Test>("integrationTest") {
    description = "Runs the functional tests"
    group = JavaBasePlugin.VERIFICATION_GROUP

    testClassesDirs = integrationTest.output.classesDirs
    classpath = integrationTest.runtimeClasspath
    mustRunAfter(tasks.test)

    reports {
        html.destination = file("${html.destination}/functional")
        junitXml.destination = file("${junitXml.destination}/functional")
    }

    timeout.set(Duration.ofMinutes(2))
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles sources JAR"
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles javaDoc JAR"
    classifier = "javadoc"
    from(tasks["javadoc"])
}

tasks {
    withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }

    withType<Test>().configureEach {
        useJUnitPlatform {
            includeEngines("spek2")
        }
    }

    check {
        dependsOn(integrationTestTask)
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    // jcenter()
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Version.kotlin}")
    implementation("com.squareup.okhttp3:okhttp:${Version.okhttp}")
    implementation("com.squareup.moshi:moshi-kotlin:${Version.moshi}")

    testImplementation(kotlin("test"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Version.spek}") {
        // exclude(group = "org.jetbrains.kotlin")
    }
    testImplementation("com.squareup.okhttp3:mockwebserver:${Version.okhttp}")

    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Version.spek}") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("org.junit.platform:junit-platform-launcher:${Version.junitPlatformVersion}")

    "integrationTestImplementation"(project)
    "integrationTestImplementation"(kotlin("test"))

    "integrationTestImplementation"("org.spekframework.spek2:spek-dsl-jvm:${Version.spek}") {
        // exclude(group = "org.jetbrains.kotlin")
    }
    "integrationTestImplementation"("com.squareup.okhttp3:mockwebserver:${Version.okhttp}")

    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Version.spek}") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    "integrationTestImplementation"("org.junit.platform:junit-platform-launcher:${Version.junitPlatformVersion}")

}

gradlePlugin {
    plugins {
        create("GsqlPlugin") {
            id = "com.optum.giraffle"
            implementationClass = "com.optum.giraffle.GsqlPlugin"
        }
    }
}

pluginBundle {
    website = webUrl
    vcsUrl = githubUrl
    description = project.description
    tags = listOf("Tigergraph", "database", "gsql", "deployment")

    plugins {
        named("GsqlPlugin") {
            displayName = "Giraffle plugin for Tigergraph"
        }
    }
}

artifacts {
    add(configurations.archives.name, sourcesJar)
    add(configurations.archives.name, javadocJar)
}

publishing {
    publications.withType<MavenPublication> {
        artifact(sourcesJar.get())

        pom {
            name.set(project.name)
            description.set(project.description)
            url.set(webUrl)

            scm {
                url.set(githubUrl)
            }

            licenses {
                license {
                    name.set("The Apache Software License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    distribution.set("repo")
                }
            }

            developers {
                developer {
                    id.set("jmeekhof")
                    name.set("Josh Meekhof")
                    email.set("joshua_meekhof@optum.com")
                }
            }
        }
    }
}

signing {
    useGpgCmd()
    sign(configurations.archives.get())
    setRequired(Callable {
        gradle.taskGraph.hasTask("pubishPlugins") &&
        (project.ext["isReleaseVersion"] as Boolean)
    })
}
