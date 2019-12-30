package com.optum.giraffle.tasks

import okhttp3.MultipartBody
import okhttp3.Request
import org.gradle.api.tasks.TaskAction

open class GsqlTokenDeleteTask() : GsqlTokenAbstract() {

    @TaskAction
    fun deleteToken() {

        val requestBody = MultipartBody.Builder()
            .addFormDataPart("secret", gsqlPluginExtension.authSecret.get())
            .addFormDataPart("token", gsqlPluginExtension.token.get())
            .build()

        val request = Request.Builder()
            .url(getHttpUrl())
            .delete(requestBody)
            .build()

        client.newCall(request).execute()
        /*
        val r = delete(url = getUrl(), params = mapOf(
            "secret" to gsqlPluginExtension.authSecret.get(),
            "token" to gsqlPluginExtension.token.get()))
        with(logger) {
            info("status code: {}", r.statusCode)
            info("response: {}", r.jsonObject)
        }
        */
    }
}
