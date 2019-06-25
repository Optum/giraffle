package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
import khttp.get
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

open class GsqlTokenTask() : DefaultTask() {

    @Internal
    private val gsqlPluginExtension: GsqlPluginExtension = project.extensions.getByType(GsqlPluginExtension::class.java)

    @get:Input
    lateinit var secret: String

    private val host: String
    private val defaultPort: String = "9000"

    init {
        this.host = gsqlPluginExtension.serverName.get()
        // logger.info("Secret is ${this.secret}")
        // this.initToken()
    }

    @TaskAction
    fun initToken() {
        logger.info("Secret is ${this.secret}")
        val url: String = "http://${this.host}:${this.defaultPort}/requesttoken"
        val r = get(url = url, params = mapOf("secret" to secret))
        logger.info("status code: {}", r.statusCode)
        logger.info("response: {}", r.jsonObject)

        when (r.jsonObject["error"]) {
            false -> gsqlPluginExtension.token.set(r.jsonObject["token"].toString())
            true -> throw GradleException(r.jsonObject["message"].toString())
        }
    }
}
