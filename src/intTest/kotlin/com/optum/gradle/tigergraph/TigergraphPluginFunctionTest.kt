package com.optum.gradle.tigergraph

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

object TigergraphPluginFunctionTest : Spek({
    val copySourcesTaskName = "gsqlCopySources"
    val gsqlShellTaskName = "gsqlShell"
    val gsqlTaskTypeName = "gsqlTaskType"

    fun Path.printFiles(): String {
        return this.toFile().walkTopDown().fold("") {
            acc: String, file: File -> "$acc\n$file"
        }
    }

    describe("Tigergraph Plugin") {
        fun execute(projectDir: File, vararg arguments: String): BuildResult {
            return GradleRunner.create()
                    .withProjectDir(projectDir)
                    .withArguments(arguments.toList())
                    .withPluginClasspath()
                    .build()
        }

        context("with plugin applied") {
            val testProjectDir: Path = Files.createTempDirectory("tigergraph_plugi_test")
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
                val buildDir: Path = testProjectDir.resolve("build/db_scripts/schema.gsql")

                assertTrue(buildDir.toFile().exists(), "files should be copied from script dir to build directory")
                assertEquals(TaskOutcome.SUCCESS, firstRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked SUCCESS")
                assertEquals(TaskOutcome.UP_TO_DATE, secondRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked UP-TO-DATE")
            }
        }

        context("plugin applied, non-standard layout") {
            val testProjectDir: Path = Files.createTempDirectory("tigergraph_plugin_test_2")
            val scriptsDir = Files.createDirectories(testProjectDir.resolve("scripts"))
            scriptsDir.toFile().mkdirs()
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            val gsqlScript = Files.createFile(testProjectDir.resolve("scripts/schema.gsql")).toFile()
            buildFile.fillFromResource("nonDefault.gradle")
            gsqlScript.fillFromResource("schema.gsql")

            it("should get scripts from non-default script directory") {
                val buildResult: BuildResult = execute(testProjectDir.toFile(), copySourcesTaskName)
                val builtScript: Path = testProjectDir.resolve("build/db_scripts/schema.gsql")

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
                val builtScript: Path = testProjectDir.resolve("build/db_scripts/schema.gsql")
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
    }
})