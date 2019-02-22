package com.optum.gradle.tigergraph

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Assert.assertThat
import org.junit.Test
import org.junit.Rule
import org.junit.Assert.assertTrue
import java.io.File
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals
import kotlin.test.asserter

class GsqlPluginTest {
    @Rule
    @JvmField
    val testProjectDir = TemporaryFolder()
    lateinit var buildFile: File

    @Test
    fun `plugin can be installed and initialized`() {

        val expected = "GSQL Plugin successfully applied"
        testProjectDir.newFile("build.gradle").fillFromResource("simple.gradle")

        val bOutput = build("tasks")
        println(bOutput.output.trimEnd())
        assertTrue(bOutput.output.contains(expected))
    }

    @Test
    fun `plugin creates gsqlCopySources task`() {
        testProjectDir.newFile("build.gradle").fillFromResource("simple.gradle")
        testProjectDir.newFolder("db_scripts")
        testProjectDir.newFile("db_scripts/schema.gsql").apply {
            writeText("""
                create vertex Name ( primary_id name STRING )
                create vertex Address ( primary_id address_id STRING )
                create directed edge lives_at (FROM Name, TO Address)
            """.trimIndent())
        }
        val bOutput = build("gsqlCopySources")
        val gsqlInput = File(testProjectDir.root, "db_scripts/schema.gsql")
        val gsqlOutput = File(testProjectDir.root, "build/db_scripts/schema.gsql")
        println(gsqlInput.toString())
        println(gsqlOutput.toString())
        println(bOutput.output)
        assert(gsqlOutput.exists())
    }

    @Test
    fun `apply gsqlCopyTask twice - should show up to date`() {
        testProjectDir.newFile("build.gradle").fillFromResource("simple.gradle")
        testProjectDir.newFolder("db_scripts")
        testProjectDir.newFile("db_scripts/schema.gsql").apply {
            writeText("""
                create vertex Name ( primary_id name STRING )
                create vertex Address ( primary_id address_id STRING )
                create directed edge lives_at (FROM Name, TO Address)
            """.trimIndent())
        }

        val taskToTest: String = ":gsqlCopySources"

        val result = build(taskToTest)
        val resultUpToDate = build(taskToTest)

        assertEquals(TaskOutcome.SUCCESS, result.task(taskToTest)!!.outcome)
        assertEquals(TaskOutcome.UP_TO_DATE, resultUpToDate.task(taskToTest)!!.outcome)

    }

    private fun build(vararg args: String): BuildResult =
            GradleRunner
                    .create()
                    .withProjectDir(testProjectDir.root)
                    .withPluginClasspath()
                    .withArguments(*args)
                    .build()

    private fun givenBuildScript(script: String) {
        newFile("build.gradle").apply {
            writeText(script)
        }
    }

    private fun newFile(fileName: String): File = testProjectDir.newFile(fileName)
}
