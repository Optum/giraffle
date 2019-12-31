package com.optum.giraffle

import java.nio.file.Files
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.gradle.testkit.runner.TaskOutcome
import org.mockserver.client.server.MockServerClient
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.Parameter.param
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe

const val antPropGroovy = "-DkotlinOrGroovy=g"
const val antPropKotlin = "-DkotlinOrGroovy=k"
const val antPropNoProperty = "-DgsqlEnablePropertiesPlugin=n"
const val antPropProperty = "-DgsqlEnablePropertiesPlugin=y"
const val copySourcesTaskName = "gsqlCopySources"
const val gsqlShellTaskName = "gsqlShell"
const val gsqlTaskTypeName = "gsqlTaskType"
const val gsqlTokenDeleteTaskName = "gsqlDeleteToken"
const val gsqlTokenTaskName = "gsqlToken"
const val outputSchemaScript = "build/db_scripts/schema.gsql"
const val restPort = 9000

object GiraffleFunctionalTest : Spek({
    describe("Giraffle Plugin") {
        val testProjectDir by memoized(CachingMode.GROUP) { Files.createTempDirectory("giraffle") }

        describe("Standard layout with db_scripts") {
            val scriptsDir by memoized(CachingMode.GROUP) {
                Files.createDirectories(testProjectDir.resolve("db_scripts")) }
            val buildFile by memoized(CachingMode.GROUP) {
                Files.createFile(testProjectDir.resolve("build.gradle")).toFile() }
            val gsqlScript by memoized(CachingMode.GROUP) {
                Files.createFile(testProjectDir.resolve("db_scripts/schema.gsql")).toFile() }

            describe("GsqlTokenTask Test") {
                var server = MockServerClient("localhost", restPort)
                beforeGroup {
                    buildFile.fillFromResource("token.gradle")
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
                    server.close()
                }

                it("getToken task should call to tigergraph, return a token, assign it to tigergraph plugin extension") {
                    val buildResult = execute(testProjectDir.toFile(), "getToken", "-i")

                    assert(buildResult.output.contains("o9fhgnc3dm9glac9e072uc6qhb0hibs6")) {
                        "getToken should output token value\n\n${buildResult.output}"
                    }
                }
            }

            describe("Test script commands") {
                beforeGroup {
                    buildFile.fillFromResource("scriptCommand.gradle")
                }
                it("should fail because neither scriptPath nor scriptCommand are specified") {
                    assertFails("Neither scriptPath nor scriptCommand specified, should fail.") {
                        execute(testProjectDir.toFile(), "showSchema")
                    }
                }
            }

            describe("listing tasks") {
                val buildResult by memoized(CachingMode.GROUP) {
                    execute(testProjectDir.toFile(), "tasks", "--all")
                }
                beforeGroup {
                    println("Before: with plugin applied group")
                    testProjectDir.printFiles()
                    scriptsDir.toFile().mkdirs()
                    buildFile.fillFromResource("simple.gradle")
                    gsqlScript.fillFromResource("schema.gsql")
                }
                afterGroup {
                    println("After: with plugin applied group")
                    testProjectDir.printFiles()
                }

                beforeEachTest {
                    println("Before test:")
                    testProjectDir.printFiles()
                }
                afterEachTest {
                    println("After test:")
                    testProjectDir.printFiles()
                }

                it("provides gsqlCopySources") {
                    assert(buildResult.output.contains(copySourcesTaskName)) {
                        "gsqlCopySources should be created\n\n${buildResult.output}"
                    }
                }
                it("provides gsqlShell") {
                    assert(buildResult.output.contains(gsqlShellTaskName)) {
                        "gsqlShell should be created\n\n${buildResult.output}"
                    }
                }
                it("provides gsqlTaskType") {
                    assert(buildResult.output.contains(gsqlTaskTypeName)) {
                        "gsqlTaskType should be created\n\n${buildResult.output}"
                    }
                }
                it("provides gsqlToken") {
                    assert(buildResult.output.contains(gsqlTokenTaskName)) {
                        "gsqlTaskType should be created\n\n${buildResult.output}"
                    }
                }
                it("provides gsqlTokenDelete") {
                    assert(buildResult.output.contains(gsqlTokenDeleteTaskName)) {
                        "gsqlTaskType should be created\n\n${buildResult.output}"
                    }
                }
            }

            describe("multi run tests") {
                val firstRun by memoized(CachingMode.GROUP) {
                    execute(testProjectDir.toFile(), copySourcesTaskName) }
                val secondRun by memoized(CachingMode.GROUP) {
                    execute(testProjectDir.toFile(), copySourcesTaskName) }
                beforeGroup {
                    scriptsDir.toFile().mkdirs()
                    buildFile.fillFromResource("simple.gradle")
                    gsqlScript.fillFromResource("schema.gsql")
                }
                // beforeEachTest { }
                it("gsqlCopySources should copy from src to build directory") {
                    firstRun.tasks.forEach {
                        println(it.path)
                    }

                    val buildDir = testProjectDir.resolve("build")
                    assertTrue(buildDir.toFile().exists(),
                        """
                            files should be copied from script dir to build directory.

                            BuildDir:
                            ${buildDir.printFiles()}

                            testProjectDir:
                            ${testProjectDir.printFiles()}
                        """.trimMargin()
                    )
                }
                it("gsqlCopySources task should be marked with SUCCESS after first run") {
                    assertEquals(TaskOutcome.SUCCESS, firstRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked SUCCESS")
                }
                it("gsqlCopySources task sholud be marked UP_TO_DATE after second run") {
                    assertEquals(TaskOutcome.UP_TO_DATE, secondRun.task(":$copySourcesTaskName")!!.outcome, "Task should be marked UP-TO-DATE")
                }
            }
        }

        describe("non standard layout") {
            val scriptsDir by memoized(CachingMode.GROUP) {
                Files.createDirectories(testProjectDir.resolve("scripts")) }
            val buildFile by memoized(CachingMode.GROUP) {
                Files.createFile(testProjectDir.resolve("build.gradle")).toFile() }
            val gsqlScript by memoized(CachingMode.GROUP) {
                Files.createFile(testProjectDir.resolve("scripts/schema.gsql")).toFile() }

            describe("copy schema from nonstandard directory") {
                beforeGroup {
                    testProjectDir.printFiles()
                    scriptsDir.toFile().mkdirs()
                    buildFile.fillFromResource("nonDefault.gradle")
                    gsqlScript.fillFromResource("schema.gsql")
                }

                val buildResult by memoized(CachingMode.GROUP) {
                    execute(testProjectDir.toFile(), copySourcesTaskName)
                }
                val builtScript by memoized(CachingMode.GROUP) {
                    testProjectDir.resolve(outputSchemaScript)
                }
                val contents by memoized(CachingMode.GROUP) {
                    builtScript.toFile().readText(Charsets.UTF_8)
                }
                it("copy schema file to build directory from non-standard folder") {
                    assertEquals(TaskOutcome.SUCCESS,
                        buildResult.task(":$copySourcesTaskName")!!.outcome,
                        "Task should be marked as SUCCESS")
                }
                it("built file should be copied to build directory") {
                    assertTrue(
                        builtScript.toFile().exists(),
                        "file should have been copied to the build directory"
                    )
                }
                it(description = "token replacements should occur throughout all script files") {

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
    }
})
