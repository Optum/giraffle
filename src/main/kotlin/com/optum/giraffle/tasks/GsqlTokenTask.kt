package com.optum.giraffle.tasks

import com.optum.giraffle.data.GsqlTokenSerializable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.IOException
import okhttp3.Request
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction

open class GsqlTokenTask : GsqlTokenAbstract() {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val tokenMoshiAdapter = moshi.adapter(GsqlTokenSerializable::class.java)

    @TaskAction
    fun initToken() {

        val x = getHttpUrl().newBuilder()
            .addQueryParameter("secret", gsqlPluginExtension.authSecret.get())
            .build()

        val r = Request.Builder()
            .url(x)
            .build()

        client.newCall(r).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code ${response.code}")
            val responseData = response.body.toString()
            logger.info("response: {}", responseData)

            val tok = tokenMoshiAdapter.fromJson(response.body!!.source())

            with(logger) {
                info("token: {}", tok!!.token)
                info("status code: {}", response.code)
                info("message: {}", tok.message)
                info("response: {}", tok.toString())
            }

            when (tok!!.error) {
                false -> gsqlPluginExtension.token.set(tok.token)
                true -> throw GradleException(tok.message)
            }
        }
    }
}
