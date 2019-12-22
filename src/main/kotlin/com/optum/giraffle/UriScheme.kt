package com.optum.giraffle

enum class UriScheme(val scheme: String) : IUriSchemePrefix {
    HTTP("http") {
        override fun getPrefix(): String = "http://"
    },
    HTTPS("https") {
        override fun getPrefix(): String = "https://"
    };

    companion object {
        fun getSchemeByName(name: String) = valueOf(name.toUpperCase())
    }
}

interface IUriSchemePrefix {
    fun getPrefix(): String
}
