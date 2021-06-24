package com.jansir.kglide.load.model


interface Headers {
    companion object {
        var DEFAULT: Headers = LazyHeaders.Builder().build()
    }
    fun getHeaders(): Map<String, String>
}