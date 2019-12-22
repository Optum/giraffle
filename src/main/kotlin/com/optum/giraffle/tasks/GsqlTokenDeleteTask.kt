package com.optum.giraffle.tasks

import khttp.delete
import org.gradle.api.tasks.TaskAction

open class GsqlTokenDeleteTask() : GsqlTokenAbstract() {

    @TaskAction
    fun deleteToken() {

        val r = delete(url = url(), params = mapOf(
            "secret" to gsqlPluginExtension.authSecret.get(),
            "token" to gsqlPluginExtension.token.get()))
        with(logger) {
            info("status code: {}", r.statusCode)
            info("response: {}", r.jsonObject)
        }
    }
}
