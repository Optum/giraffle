package com.optum.giraffle

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.Parameter.param
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

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

    val antProps: Array<String> = arrayOf(
        "gsqlNewProject",
        "-DgsqlGraphname=testApp",
        "-DgsqlHost=myhost",
        "-DgsqlAdminUserName=tiger",
        "-DgsqlAdminPassword=tig3r",
        "-DgsqlUserName=joe_user",
        "-DgsqlPassword=s3cr3t"
    )

    val antPropKotlin = "-DkotlinOrGroovy=k"
    val antPropGroovy = "-DkotlinOrGroovy=g"
    val antPropProperty = "-DgsqlEnablePropertiesPlugin=y"
    val antPropNoProperty = "-DgsqlEnablePropertiesPlugin=n"

    val restPort = 9000

    var server = MockServerClient("localhost", restPort)

    beforeGroup {
        println("This is setup")
        server = ClientAndServer.startClientAndServer(restPort)
        server.`when`(
            request()
                .withMethod("GET")
                .withPath("/requesttoken")
                .withQueryStringParameters(param("secret", "b2cd023976abe4855e675b23677adda8"))
        )
        .respond(
            response()
                .withBody("{\"code\":\"REST-0000\",\"expiration\":1580254963,\"error\":false,\"message\":\"Generate new token successfully.\",\"token\":\"o9fhgnc3dm9glac9e072uc6qhb0hibs6\"}")
        )
    }

    afterGroup {
        println("This is teardown")
        server.close()
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

        context("plugin applied, standard layout, no scripts") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_plugin_test")
            val scriptsDir = Files.createDirectories(testProjectDir.resolve("db_scripts"))
            scriptsDir.toFile().mkdirs()
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            buildFile.fillFromResource("scriptCommand.gradle")

            it("should fail because neither scriptPath nor scriptCommand are specified") {
                assertFails("Neither scriptPath nor scriptCommand specified, should fail.") {
                    execute(testProjectDir.toFile(), "showSchema")
                }
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

        context("GsqlTokenTask Test") {
            val testProjectDir: Path = Files.createTempDirectory("giraffle_tokentask")
            val buildFile = Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
            buildFile.fillFromResource("token.gradle")

            it("getToken task should call to tigergraph, return a token, assign it to tigergraph plugin extension") {
                val buildResult: BuildResult = execute(testProjectDir.toFile(), "getToken", "-i")

                assert(buildResult.output.contains("o9fhgnc3dm9glac9e072uc6qhb0hibs6")) {
                    "getToken should output token value\n\n${buildResult.output}"
                }
            }
        }
    }
})
