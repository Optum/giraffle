package com.optum.giraffle

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object GiraffleNewProjectTest : Spek({
    val antProps: Array<String> = arrayOf(
        "gsqlNewProject",
        "-DgsqlGraphname=testApp",
        "-DgsqlHost=myhost",
        "-DgsqlAdminUserName=tiger",
        "-DgsqlAdminPassword=tig3r",
        "-DgsqlUserName=joe_user",
        "-DgsqlPassword=s3cr3t"
    )

    describe("Giraffle Plugin") {
        context("New Project wizard - Kotlin") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_new_proj_wiz_kotlin_dsl")
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            buildFile.fillFromResource("newProject.gradle")

            it("should create property files") {
                val myAntProp = antProps.plus(antPropKotlin).plus(antPropProperty)

                execute(testProjectDir.toFile(), *myAntProp)
                val gradlePropFile = File(testProjectDir.toFile(), "gradle.properties")
                val gradleLocalPropFile = File(testProjectDir.toFile(), "gradle-local.properties")
                val gitIgnoreFile = File(testProjectDir.toFile(), ".gitignore")
                val gradleContents: String = gradlePropFile.readText()
                val gradleLocalContents: String = gradleLocalPropFile.readText()
                val credCheck: (String) -> Boolean = { contents: String ->
                    with(contents) {
                        contains("gAdminUserName=tiger") and
                            contains("gAdminPassword=tig3r") and
                            contains("gUserName=joe_user") and
                            contains("gPassword=s3cr3t")
                    }
                }

                assertTrue("gradle.properties doesn't exist") {
                    gradlePropFile.exists()
                }
                assertFalse("gradle.properties shouldn't contain passwords") {
                    credCheck(gradleContents)
                }
                assertTrue("gradle-local.properties doesn't exist") {
                    gradleLocalPropFile.exists()
                }
                assertTrue("gradle-local.properties should contain credentials") {
                    credCheck(gradleLocalContents)
                }
                assertTrue(".gitignore should exist") {
                    gitIgnoreFile.exists()
                }
            }

            it("should create kotlin build file") {
                val myAntProp = antProps.plus(antPropKotlin).plus(antPropProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                assertTrue {
                    File(testProjectDir.toFile(), "build.gradle.kts").exists()
                }
            }

            it("kotlin build should use properties plugin") {
                val myAntProp = antProps.plus(antPropKotlin).plus(antPropProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                val propertyBuildFile = File(testProjectDir.toFile(), "build.gradle.kts")

                assertTrue("Build file should contain net.saliman.properties plugin.") {
                    propertyBuildFile.readText().contains("net.saliman.properties")
                }

                assertTrue("gradle-local.properties file should exist.") {
                    File(testProjectDir.toFile(), "gradle-local.properties").exists()
                }
            }

            it("kotlin should not use properties plugin") {
                val myAntProp = antProps.plus(antPropKotlin).plus(antPropNoProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                assertFalse("Build file should not contain net.saliman.properties plugin") {
                    File(testProjectDir.toFile(), "build.gradle.kts").readText().contains("net.saliman.properties")
                }
            }
        }

        context("New project wizard - Groovy") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_new_proj_wiz_groovy_dsl")
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            buildFile.fillFromResource("newProject.gradle")

            it("groovy build should use properties plugin") {
                val myAntProp = antProps.plus(antPropGroovy).plus(antPropProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                val propertyBuildFile = File(testProjectDir.toFile(), "build.gradle")

                assertTrue("Build file should contain net.saliman.properties plugin.\n${testProjectDir.toFile()}+") {
                    propertyBuildFile.readText().contains("net.saliman.properties")
                }

                assertTrue("gradle-local.properties file should exist.") {
                    File(testProjectDir.toFile(), "gradle-local.properties").exists()
                }
            }

            it("groovy should not use properties plugin") {
                val myAntProp = antProps.plus(antPropGroovy).plus(antPropNoProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                assertFalse("Build file should not contain net.saliman.properties plugin") {
                    File(testProjectDir.toFile(), "build.gradle").readText().contains("net.saliman.properties")
                }
            }
        }
    }
})
