package com.optum.giraffle.tasks

import khttp.delete
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

open class GsqlTokenDeleteTask() : GsqlTokenAbstract() {

    @get:Input
    lateinit var token: String

    @TaskAction
    fun deleteToken() {

        val r = delete(url = url(), params = mapOf("secret" to secret, "token" to token))
        with(logger) {
            info("status code: {}", r.statusCode)
            info("response: {}", r.jsonObject)
        }
    }
}
