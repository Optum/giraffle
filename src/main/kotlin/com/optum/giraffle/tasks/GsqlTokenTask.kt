package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
// import com.optum.giraffle.data.GsqlToken
import khttp.get
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
// import org.gradle.api.tasks.Nested
// import org.gradle.api.tasks.TaskAction

open class GsqlTokenTask() : DefaultTask() {

    // TODO: Remove this type, add the token to the gsqlPluginExtension
    // @Output
    // val gsqlToken: GsqlToken = GsqlToken(project)

    @Internal
    private val gsqlPluginExtension: GsqlPluginExtension = project.extensions.getByType(GsqlPluginExtension::class.java)

    @Input
    lateinit var secret: String

    private val host: String
    private val defaultPort: String = "9000"

    init {
        this.host = gsqlPluginExtension.serverName.get()
        this.initToken()
    }

    fun initToken() {
        val url: String = "http://${this.host}:${this.defaultPort}/requesttoken"
        val r = get(url = url, params = mapOf("secret" to secret))
        logger.info("status code: {}", r.statusCode)
        logger.info("response: {}", r.jsonObject)

        gsqlPluginExtension.token.set(r.jsonObject["code"].toString())

        // gsqlToken.setCode(r.jsonObject["code"])
    }
}
