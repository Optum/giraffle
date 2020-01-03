package com.optum.giraffle.tasks

import com.optum.giraffle.GsqlPluginExtension
import com.optum.giraffle.UriScheme
import com.optum.giraffle.data.GsqlTokenSerializable
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

abstract class GsqlTokenAbstract : DefaultTask() {

    @Internal
    protected val client: OkHttpClient = OkHttpClient()

    @Internal
    protected val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    @Internal
    protected val tokenMoshiAdapter = moshi.adapter(GsqlTokenSerializable::class.java)

    @Input
    protected val gsqlPluginExtension: GsqlPluginExtension =
        project.extensions.getByType(GsqlPluginExtension::class.java)

    @Input
    val uriScheme: Property<UriScheme> = project.objects.property(UriScheme::class.java)

    @Input
    val host: Property<String> = project.objects.property(String::class.java)

    @Input
    val defaultPort: Property<Int> = project.objects.property(Int::class.java)

    @Input
    fun getUrl(): String = getHttpUrl().toString()

    @Input
    fun getHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
            .scheme(uriScheme.get().scheme)
            .host(host.get())
            .port(defaultPort.get())
            .addPathSegment("requesttoken")
            .build()
    }
}
