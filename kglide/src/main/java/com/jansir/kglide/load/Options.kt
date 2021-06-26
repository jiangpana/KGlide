package com.jansir.kglide.load

import androidx.collection.SimpleArrayMap
import com.jansir.kglide.util.CachedHashCodeArrayMap
import java.security.MessageDigest


class Options : Key {

    private val values =
        CachedHashCodeArrayMap<Option<*>, Any>()

    operator fun <T> get(option: Option<T>): T? {
        return if (values.containsKey(option)) values[option] as T? else option.defaultValue
    }
    operator fun <T> set(option: Option<T>, value: T): Options{
        values[option] = value
        return this
    }

    fun putAll(other: Options) {
        values.putAll(other.values as SimpleArrayMap<Option<*>, Any>)
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        values.forEach {
            val key =it.key
            val value =it.value
           updateDiskCacheKey(
                key,
                value,
                messageDigest
            )
        }
    }
    private fun <T> updateDiskCacheKey(
        option: Option<T>, value: Any, md: MessageDigest
    ) {
        option.update(value as T, md)
    }

    override fun equals(o: Any?): Boolean {
        if (o is Options) {
            return values == o.values
        }
        return false
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }
}