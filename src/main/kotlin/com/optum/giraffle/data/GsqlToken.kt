package com.optum.giraffle.data

import java.io.Serializable
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional

class GsqlToken(project: Project) {
    private val code: Property<String> = project.objects.property(String::class.java)
    private val expiration: Property<Int> = project.objects.property(Int::class.java)
    private val error: Property<Boolean> = project.objects.property(Boolean::class.java)
    private val token: Property<String> = project.objects.property(String::class.java)

    @Input
    @Optional
    fun getCode(): String? = code.orNull

    fun setCode(code: Provider<String>) = this.code.set(code)

    @Input
    @Optional
    fun getExpiration(): Int? = expiration.orNull

    fun setExpiration(expire: Provider<Int>) = this.expiration.set(expire)

    @Input
    @Optional
    fun getError(): Boolean = error.getOrElse(false)

    fun setError(error: Provider<Boolean>) = this.error.set(error)

    @Input
    fun getToken(): String = token.get()

    fun setToken(token: Provider<String>) = this.token.set(token)
}

data class GsqlTokenSerializable(
    @get:Input val code: String,
    @get:Input val expiration: String,
    @get:Input val error: Boolean,
    @get:Input val token: String
) : Serializable
