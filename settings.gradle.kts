
rootProject.name = "giraffle"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.0"
}
org.apache.tools.ant.DirectoryScanner.removeDefaultExclude("**/.gitignore")
