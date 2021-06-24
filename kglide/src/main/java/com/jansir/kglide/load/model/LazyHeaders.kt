package com.jansir.kglide.load.model

import android.text.TextUtils
import java.util.*
import kotlin.collections.HashMap


class LazyHeaders(val lazyHeaders: Map<String, List<LazyHeaderFactory>>) : Headers {

    private val combinedHeadersby by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        generateHeaders()
    }

    private fun generateHeaders(): Map<String, String> {
        val combinedHeaders: MutableMap<String, String> =
            HashMap()
        for ((key, value) in lazyHeaders.entries) {
            val values: String = buildHeaderValue(value)
            if (!TextUtils.isEmpty(values)) {
                combinedHeaders[key] = values
            }
        }
        return combinedHeaders
    }

    private fun buildHeaderValue(factories: List<LazyHeaderFactory>): String {
        val sb = StringBuilder()
        val size = factories.size
        for (i in 0 until size) {
            val factory = factories[i]
            val header = factory.buildHeader()
            if (!TextUtils.isEmpty(header)) {
                sb.append(header)
                if (i != factories.size - 1) {
                    sb.append(',')
                }
            }
        }
        return sb.toString()
    }

    override fun equals(o: Any?): Boolean {
        if (o is LazyHeaders) {
            return lazyHeaders == o.lazyHeaders
        }
        return false
    }

    override fun hashCode(): Int {
        return lazyHeaders.hashCode()
    }


    override fun getHeaders(): Map<String, String> {
        return combinedHeadersby
    }

    class Builder {

        companion object {
            private const val USER_AGENT_HEADER = "User-Agent"
            private val DEFAULT_USER_AGENT: String = getSanitizedUserAgent()
            private var DEFAULT_HEADERS = HashMap<String, List<LazyHeaderFactory>>()
            private fun getSanitizedUserAgent(): String {
                val defaultUserAgent = System.getProperty("http.agent")
                if (TextUtils.isEmpty(defaultUserAgent)) {
                    return defaultUserAgent
                }
                val length = defaultUserAgent.length
                val sb = java.lang.StringBuilder(defaultUserAgent.length)
                for (i in 0 until length) {
                    val c = defaultUserAgent[i]
                    if ((c > '\u001f' || c == '\t') && c < '\u007f') {
                        sb.append(c)
                    } else {
                        sb.append('?')
                    }
                }
                return sb.toString()
            }


            init {
                if (!TextUtils.isEmpty(DEFAULT_USER_AGENT)) {
                    DEFAULT_HEADERS[USER_AGENT_HEADER] =
                        mutableListOf(LazyHeaders.StringHeaderFactory(DEFAULT_USER_AGENT))
                }

            }
        }

        private var copyOnModify = true
        private var headers: HashMap<String, List<LazyHeaderFactory>> = DEFAULT_HEADERS
        private var isUserAgentDefault = true

        fun addHeader(
            key: String,
            value: String
        ): Builder {
            return addHeader(key, StringHeaderFactory(value))
        }

        fun addHeader(
            key: String,
            factory: LazyHeaderFactory
        ): Builder {
            if (isUserAgentDefault && USER_AGENT_HEADER.equals(
                    key,
                    ignoreCase = true
                )
            ) {
                return setHeader(key, factory)
            }
            copyIfNecessary()
            getFactories(key).add(factory)
            return this
        }

        fun setHeader(
            key: String,
            value: String?
        ): Builder {
            return setHeader(key, value?.let { StringHeaderFactory(it) })
        }

        fun setHeader(
            key: String,
            factory: LazyHeaderFactory?
        ): Builder {
            copyIfNecessary()
            if (factory == null) {
                headers.remove(key)
            } else {
                val factories = getFactories(key)
                factories.clear()
                factories.add(factory)
            }
            if (isUserAgentDefault && USER_AGENT_HEADER.equals(
                    key,
                    ignoreCase = true
                )
            ) {
                isUserAgentDefault = false
            }
            return this
        }

        private fun getFactories(key: String): MutableList<LazyHeaderFactory> {
            var factories: MutableList<LazyHeaderFactory>? =
                headers[key] as MutableList<LazyHeaderFactory>?
            if (factories == null) {
                factories = ArrayList()
                headers.put(key, factories)
            }
            return factories
        }

        private fun copyIfNecessary() {
            if (copyOnModify) {
                copyOnModify = false
                headers = copyHeaders()
            }
        }

        private fun copyHeaders(): HashMap<String, List<LazyHeaderFactory>> {
            val result = HashMap<String, List<LazyHeaderFactory>>(headers.size)
            for ((key, value) in headers) {
                val valueCopy: List<LazyHeaderFactory> =
                    ArrayList(value)
                result[key] = valueCopy
            }
            return result
        }

        fun build(): LazyHeaders {
            copyOnModify = true
            return LazyHeaders(headers)
        }

    }


    internal class StringHeaderFactory(private val value: String) : LazyHeaderFactory {
        override fun buildHeader(): String {
            return value
        }

        override fun toString(): String {
            return "StringHeaderFactory{value='$value'}"
        }

        override fun equals(o: Any?): Boolean {
            if (o is StringHeaderFactory) {
                return value == o.value
            }
            return false
        }

        override fun hashCode(): Int {
            return value.hashCode()
        }

    }
}