package com.jansir.kglide.load

import java.security.MessageDigest

class Option<T> private constructor(
    val key: String,
    val defaultValue: T? = null,
    val cacheKeyUpdater: CacheKeyUpdater<T>
) {


    companion object {

        val EMPTY_UPDATER = object : CacheKeyUpdater<Any> {
            override fun update(keyBytes: ByteArray, value: Any, messageDigest: MessageDigest) {
            }
        }

        fun <T> memory(key: String): Option<T> {
            return Option(key, null, EMPTY_UPDATER as CacheKeyUpdater<T>);
        }

        fun <T> memory(key: String, defaultValue: T): Option<T> {
            return Option(key, defaultValue, EMPTY_UPDATER as CacheKeyUpdater<T>);
        }

        fun <T> disk(
            key: String, cacheKeyUpdater: CacheKeyUpdater<T>
        ): Option<T> {
            return Option(key, null, cacheKeyUpdater)
        }

        fun <T> disk(
            key: String, defaultValue: T, cacheKeyUpdater: CacheKeyUpdater<T>
        ): Option<T> {
            return Option(key, defaultValue, cacheKeyUpdater)
        }
    }

    fun update(value: T, messageDigest: MessageDigest) {
        cacheKeyUpdater.update(getKeyBytes(), value, messageDigest)
    }

    @Volatile
    private var keyBytes: ByteArray? = null
    private fun getKeyBytes(): ByteArray {
        if (keyBytes == null) {
            keyBytes = key.toByteArray(Key.CHARSET)
        }
        return keyBytes!!
    }

    override fun equals(other: Any?): Boolean {
        if (other is Option<*>) {
            return key == other.key
        }
        return false
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }


    interface CacheKeyUpdater<T> {
        fun update(
            keyBytes: ByteArray,
            value: T,
            messageDigest: MessageDigest
        )
    }
}