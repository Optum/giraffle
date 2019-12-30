package com.optum.giraffle.tasks

import com.optum.giraffle.data.GToken
import com.optum.giraffle.data.GsqlToken
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import khttp.get
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

open class GsqlTokenTask() : GsqlTokenAbstract() {

    private val client: OkHttpClient = OkHttpClient()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val tokenMoshiAdapter = moshi.adapter(Token::class.java)

    @TaskAction
    fun initToken() {

        val x = httpUrl.newBuilder()
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

            // val jsonData = JSONObject(responseData)

            with(logger) {
                info("token: {}", tok!!.token)
                info("status code: {}", response.code)
                info("message: {}", tok!!.message)
                info("response: {}", tok.toString())
            }

            when (tok!!.error) {
                false -> gsqlPluginExtension.token.set(tok.token)
                true -> throw tok.message?.let { GradleException(it) }!!
            }

            /*
            when (jsonData["error"]) {
                false -> gsqlPluginExtension.token.set(jsonData["token"].toString())
                true -> throw GradleException(jsonData["message"].toString())
            }
             */
        }

        /*
        val r = Request.Builder()
            .url(url)

        val r = get(url = url, params = mapOf(
            "secret" to gsqlPluginExtension.authSecret.get()
        ))
        with(logger) {
            // info("generated url: {}", r.url)
            info("status code: {}", r.statusCode)
            info("response: {}", r.jsonObject
        }

        when (r.jsonObject["error"]) {
            false -> gsqlPluginExtension.token.set(r.jsonObject["token"].toString())
            true -> throw GradleException(r.jsonObject["message"].toString())
        }
         */
    }
}

@JsonClass(generateAdapter = true)
data class Token(
    var code: String?,
    var expiration: Int?,
    var error: Boolean?,
    var message: String?,
    var token: String?
)
