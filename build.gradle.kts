import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    // id("org.jetbrains.kotlin.jvm") version Versions.kotlin
    `build-scan`
    `java-gradle-plugin`
    `maven-publish`
    // signing
    // id("com.gradle.plugin-publish") version "0.9.10"
    id("gradle.site") version Versions.site
    kotlin("jvm") version Versions.kotlin
    id("org.jmailen.kotlinter") version Versions.kotlinter
}

group = "com.optum.giraffle"
version = "0.1.0"
description = "Provides dsl and support for connection to Tigergraph servers, and executing scripts against Tigergraph."

val webUrl = ""
val githubUrl = "https://github.optum.com/ATC/${project.name}.git"

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"

    publishAlways()
}

site {
    outputDir.set(file("$rootDir/docs"))
    websiteUrl.set(webUrl)
    vcsUrl.set(githubUrl)
}

gradlePlugin {
    plugins {
        create("GsqlPlugin") {
            id = "com.optum.gradle.giraffle"
            implementationClass = "com.optum.giraffle.GsqlPlugin"
        }

    }
}

val intTest by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + configurations.testRuntime.get()
    runtimeClasspath += output + compileClasspath
}

val intTestImplementation by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val intTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.testRuntimeOnly.get())
}

val integrationTest by tasks.registering(Test::class) {
    description = "Runs the functional tests"
    group = JavaBasePlugin.VERIFICATION_GROUP

    testClassesDirs = intTest.output.classesDirs
    classpath = intTest.runtimeClasspath
    shouldRunAfter(tasks.test)

    reports {
        html.destination = file("${html.destination}/functional")
        junitXml.destination = file("${junitXml.destination}/functional")
    }

    timeout.set(Duration.ofMinutes(2))
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
        dependsOn(integrationTest.get())
    }
}

repositories {
    // Use jcenter for resolving your dependencies.
    // You can declare any Maven/Ivy/file repository here.
    jcenter()
    mavenLocal()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test"))
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spek}") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testRuntimeOnly(kotlin("reflect"))
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("org.junit.platform:junit-platform-launcher:${Versions.junitPlatformVersion}")

    intTestImplementation(gradleTestKit())
}

publishing {
    repositories {}
    publications {}
}
