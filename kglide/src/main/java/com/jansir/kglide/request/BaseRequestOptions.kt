package com.jansir.kglide.request

import com.jansir.kglide.ext.isSet

abstract class BaseRequestOptions<T : BaseRequestOptions<T>> {
    private val UNSET = -1
    private val SIZE_MULTIPLIER = 1 shl 1
    private val DISK_CACHE_STRATEGY = 1 shl 2
    private val PRIORITY = 1 shl 3
    private val ERROR_PLACEHOLDER = 1 shl 4
    private val ERROR_ID = 1 shl 5
    private val PLACEHOLDER = 1 shl 6
    private val PLACEHOLDER_ID = 1 shl 7
    private val IS_CACHEABLE = 1 shl 8
    private val OVERRIDE = 1 shl 9
    private val SIGNATURE = 1 shl 10
    private val TRANSFORMATION = 1 shl 11
    private val RESOURCE_CLASS = 1 shl 12
    private val FALLBACK = 1 shl 13
    private val FALLBACK_ID = 1 shl 14
    private val THEME = 1 shl 15
    private val TRANSFORMATION_ALLOWED = 1 shl 16
    private val TRANSFORMATION_REQUIRED = 1 shl 17
    private val USE_UNLIMITED_SOURCE_GENERATORS_POOL = 1 shl 18
    private val ONLY_RETRIEVE_FROM_CACHE = 1 shl 19
    private val USE_ANIMATION_POOL = 1 shl 20

    private val fields = 0

    private val overrideHeight: Int = UNSET
    private val overrideWidth: Int = UNSET
    private var isTransformationAllowed = true

    fun isTransformationSet(): Boolean {
        return fields.isSet(TRANSFORMATION)
    }

    fun isTransformationAllowed(): Boolean {
        return isTransformationAllowed
    }

}