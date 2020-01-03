package com.optum.giraffle.tasks

import okhttp3.Request
import org.gradle.api.tasks.TaskAction

open class GsqlTokenDeleteTask() : GsqlTokenAbstract() {

    @TaskAction
    fun deleteToken() {

        val delUrl = getHttpUrl().newBuilder()
            .addQueryParameter("secret", gsqlPluginExtension.authSecret.get())
            .addQueryParameter("token", gsqlPluginExtension.token.get())
            .build()

        val request = Request.Builder()
            .url(delUrl)
            .delete()
            .build()

        client.newCall(request).execute().use { response ->
            val tok = tokenMoshiAdapter.fromJson(response.body!!.source())

            with(logger) {
                info("token: {}", tok!!.token)
                info("status code: {}", response.code)
                info("message: {}", tok.message)
                info("response: {}", tok.toString())
            }
        }
    }
}
