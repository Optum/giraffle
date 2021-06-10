package com.optum.giraffle

import okhttp3.mockwebserver.MockWebServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.lifecycle.CachingMode
import org.spekframework.spek2.style.specification.describe
import java.nio.file.Files

object GiraffleHttpTest : Spek({
    val server = MockWebServer()
    val dispatcher = SuccessDispatcher()
    val testProjectDir by memoized(CachingMode.EACH_GROUP) { Files.createTempDirectory("giraffle") }
    val buildFile by memoized(CachingMode.EACH_GROUP) {
        Files.createFile(testProjectDir.resolve("build.gradle")).toFile()
    }

    describe("Giraffle Token Task Test") {
        beforeGroup {
            server.dispatcher = dispatcher
            server.start(restPort)
            buildFile.fillFromResource("token.gradle")
        }
        afterGroup {
            server.shutdown()
        }
        it("getToken should call tigergraph, return a token, assign it to tigergraph extension") {
            val buildResult = execute(testProjectDir.toFile(), "gsqlToken", "-i")
            assert(buildResult.output.contains("o9fhgnc3dm9glac9e072uc6qhb0hibs6")) {
                "getToken should output token value\n\n${buildResult.output}"
            }
        }
    }
})
