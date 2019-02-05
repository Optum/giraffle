package com.optum.gradle.tigergraph

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.Test
import org.junit.Rule
import org.junit.Assert.assertTrue
import java.io.File
import org.junit.rules.TemporaryFolder

class GsqlPluginTest {
    @Test
    fun `plugin can be installed and initialized`() {

        val expected = "GSQL Plugin successfully applied"
        givenBuildScript(
                """
                plugins {
                    id("com.optum.gradle.tigergraph")
                }
            """.trimIndent()
        )

        val bOutput = build("tasks")
        println(bOutput.output.trimEnd())
        assertTrue(bOutput.output.contains(expected))
    }

    @Test
    fun `plugin creates gsqlCopySources task`() {
        givenBuildScript("""
            plugins {
                id("com.optum.gradle.tigergraph")
            }
        """.trimIndent()
        )
        temporaryFolder.newFolder("db_scripts")
        temporaryFolder.newFile("db_scripts/schema.gsql").apply {
            writeText("""
                create vertex Name ( primary_id name STRING )
                create vertex Address ( primary_id address_id STRING )
                create directed edge lives_at (FROM Name, TO Address)
            """.trimIndent())
        }
        val bOutput = build("gsqlCopySources")
        val gsqlInput = File(temporaryFolder.root, "db_scripts/schema.gsql")
        val gsqlOutput = File(temporaryFolder.root, "build/db_scripts/schema.gsql")
        println(gsqlInput.toString())
        println(gsqlOutput.toString())
        println(bOutput.output)
        assert(gsqlOutput.exists())
    }

    private fun build(vararg args: String): BuildResult =
            GradleRunner
                    .create()
                    .withProjectDir(temporaryFolder.root)
                    .withPluginClasspath()
                    .withArguments(*args)
                    .build()

    private fun givenBuildScript(script: String) {
        newFile("build.gradle").apply {
            writeText(script)
        }
    }

    private fun newFile(fileName: String): File = temporaryFolder.newFile(fileName)

    @Rule
    @JvmField
    val temporaryFolder = TemporaryFolder()
}
