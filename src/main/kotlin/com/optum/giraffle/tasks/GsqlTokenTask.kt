package com.optum.giraffle.tasks

import khttp.get
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

open class GsqlTokenTask() : GsqlTokenAbstract() {

    @TaskAction
    fun initToken() {

        val r = get(url = url(), params = mapOf("secret" to secret))
        with(logger) {
            // info("generated url: {}", r.url)
            info("status code: {}", r.statusCode)
            info("response: {}", r.jsonObject)
        }

        when (r.jsonObject["error"]) {
            false -> gsqlPluginExtension.token.set(r.jsonObject["token"].toString())
            true -> throw GradleException(r.jsonObject["message"].toString())
        }
    }
}
