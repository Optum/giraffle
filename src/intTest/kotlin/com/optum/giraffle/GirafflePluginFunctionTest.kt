package com.optum.giraffle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object GirafflePluginFunctionTest : Spek({
    val copySourcesTaskName = "gsqlCopySources"
    val gsqlShellTaskName = "gsqlShell"
    val gsqlTaskTypeName = "gsqlTaskType"
    val outputSchemaScript = "build/db_scripts/schema.gsql"

    fun Path.printFiles(): String {
        return this.toFile().walkTopDown().fold("") {
            acc: String, file: File -> "$acc\n$file"
        }
    }

    describe("Giraffle Plugin") {
        fun execute(projectDir: File, vararg arguments: String): BuildResult {
            return GradleRunner.create()
                    .withProjectDir(projectDir)
                    .withArguments(arguments.toList())
                    .withPluginClasspath()
                    .build()
        }

        context("with plugin applied") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_plugin_test")
            val scriptsDir = Files.createDirectories(testProjectDir.resolve("db_scripts"))
            scriptsDir.toFile().mkdirs()
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            val gsqlScript = Files.createFile(testProjectDir.resolve("db_scripts/schema.gsql")).toFile()
            buildFile.fillFromResource("simple.gradle")
            gsqlScript.fillFromResource("schema.gsql")

            it("provides GsqlCopySources, GsqShell, and GsqlTaskType task") {
                val buildResult: BuildResult = execute(testProjectDir.toFile(), "tasks", "--all")
                assert(buildResult.output.contains(copySourcesTaskName)) { "gsqlCopySources should be created" }
                assert(buildResult.output.contains(gsqlShellTaskName)) { "gsqlShell should be created" }
                assert(buildResult.output.contains(gsqlTaskTypeName)) { "gsqlTaskType should be created\n\n${buildResult.output}" }
            }

            it("gsqlCopySources should copy sources to build directory and should when run twice mark the second run UP-TO-DATE") {
                val firstRun: BuildResult = execute(testProjectDir.toFile(), copySourcesTaskName)
                val secondRun: BuildResult = execute(testProjectDir.toFile(), copySourcesTaskName)
                val buildDir: Path = testProjectDir.resolve(outputSchemaScript)

                assertTrue(buildDir.toFile().exists(), "files should be copied from script dir to build directory")
                assertEquals(TaskOutcome.SUCCESS, firstRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked SUCCESS")
                assertEquals(TaskOutcome.UP_TO_DATE, secondRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked UP-TO-DATE")
            }
        }

        context("plugin applied, non-standard layout") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_plugin_test")
            val scriptsDir = Files.createDirectories(testProjectDir.resolve("scripts"))
            scriptsDir.toFile().mkdirs()
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            val gsqlScript = Files.createFile(testProjectDir.resolve("scripts/schema.gsql")).toFile()
            buildFile.fillFromResource("nonDefault.gradle")
            gsqlScript.fillFromResource("schema.gsql")

            it("should get scripts from non-default script directory") {
                val buildResult: BuildResult = execute(testProjectDir.toFile(), copySourcesTaskName)
                val builtScript: Path = testProjectDir.resolve(outputSchemaScript)

                assertTrue(
                        actual = builtScript.toFile().exists(),
                        message = "file should have been copied from requested script directory\n${testProjectDir.printFiles()}"
                )
                assertEquals(
                        TaskOutcome.SUCCESS,
                        buildResult.task(":$copySourcesTaskName")!!.outcome,
                        "Should have been marked SUCCESS"
                )
            }
            it(description = "token replacements should occur throughout all script files") {
                execute(testProjectDir.toFile(), copySourcesTaskName)
                val builtScript: Path = testProjectDir.resolve(outputSchemaScript)
                val contents: String = builtScript.toFile().readText(Charsets.UTF_8)

                assertTrue(
                        actual = contents.contains("abc"),
                        message = "Token replacement should have replaced @graphname@."
                )
                assertFalse(
                        actual = contents.contains("@graphname@"),
                        message = "Token replacement should have replaced @graphname@."
                )
            }
        }

        context("New Project wizard") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_plugin_test")
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            buildFile.fillFromResource("newProject.gradle")
            val antProps: Array<String> = arrayOf(
                "gsqlNewProject",
                "-DgsqlGraphname=testApp",
                "-DgsqlHost=myhost",
                "-DgsqlAdminUserName=tiger",
                "-DgsqlAdminPassword=tig3r",
                "-DgsqlUserName=joe_user",
                "-DgsqlPassword=s3cr3t"
            )

            val antPropsKotlinProperty: Array<String> = arrayOf(
                "-DgsqlEnablePropertiesPlugin=y",
                "-DkotlinOrGroovy=k"
            )

            it("should create property files") {
                val myAntProp = antProps.plus(antPropsKotlinProperty)

                execute(testProjectDir.toFile(), *myAntProp)
                val gradlePropFile = File(testProjectDir.toFile(), "gradle.properties")
                val gradleLocalPropFile = File(testProjectDir.toFile(), "gradle-local.properties")
                val gitIgnoreFile = File(testProjectDir.toFile(), ".gitignore")
                val gradleContents: String = gradlePropFile.readText()
                val gradleLocalContents: String = gradleLocalPropFile.readText()
                val credCheck: (String) -> Boolean  = { contents: String ->
                    with(contents) {
                        contains("gsqlAdminUserName=tiger") and
                        contains("gsqlAdminPassword=tig3r") and
                        contains("gsqlUserName=joe_user") and
                        contains("gsqlPassword=s3cr3t")
                    }
                }

                assertTrue( message = "gradle.properties doesn't exist" ) {
                    gradlePropFile.exists()
                }
                assertFalse(message = "gradle.properties shouldn't contain passwords") {
                    credCheck(gradleContents)
                }
                assertTrue( message = "gradle-local.properties doesn't exist" ) {
                    gradleLocalPropFile.exists()
                }
                assertTrue(message = "gradle-local.properties should contain credentials") {
                    credCheck(gradleLocalContents)
                }
                assertTrue(message = ".gitignore should exist") {
                    gitIgnoreFile.exists()
                }

            }

            it("should create kotlin build file") {
                val myAntProp = antProps.plus(antPropsKotlinProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                assertTrue {
                    File(testProjectDir.toFile(),"build.gradle.kts").exists()
                }
            }

            it("should use properties plugin") {
                val myAntProp = antProps.plus(antPropsKotlinProperty)
                execute(testProjectDir.toFile(), *myAntProp)
                val propertyBuildFile = File(testProjectDir.toFile(),"build.gradle.kts")

                assertTrue("Build file should contain net.saliman.properties plugin.") {
                    propertyBuildFile.readText().contains("net.saliman.properties")
                }

                assertTrue("gradle-local.properties file should exist.") {
                    File(testProjectDir.toFile(),"gradle-local.properties").exists()
                }

            }

        }
    }
})
