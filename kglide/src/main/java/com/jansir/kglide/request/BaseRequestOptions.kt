package com.jansir.kglide.request

import android.content.res.Resources.Theme
import android.graphics.drawable.Drawable
import androidx.core.util.Preconditions
import com.jansir.kglide.Priority
import com.jansir.kglide.ext.isSet
import com.jansir.kglide.ext.unSet
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Option
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.engine.DiskCacheStrategy
import com.jansir.kglide.load.resource.bitmap.DownsampleStrategy
import com.jansir.kglide.signature.EmptySignature

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
    private var fields = 0

    private var errorId = 0
    private var diskCacheStrategy = DiskCacheStrategy.RESOURCE
    private var priority = Priority.NORMAL
    private var overrideHeight: Int = UNSET
    private var overrideWidth: Int = UNSET
    private var isTransformationAllowed = true
    private var useUnlimitedSourceGeneratorsPool = false
    private var onlyRetrieveFromCache = false
    private var isScaleOnlyOrNoTransform = true
    private var useAnimationPool = false
    private var isTransformationRequired = false
    private var options = Options()

    private var resourceClass = Any::class.java
    private var sizeMultiplier = 1f
    private var errorPlaceholder: Drawable? = null
    private var placeholderDrawable: Drawable? = null
    private var fallbackDrawable: Drawable? = null
    private var placeholderId = 0
    private var fallbackId = 0
    private var isCacheable = true
    private var signature: Key = EmptySignature.obtain()
    private var theme: Theme? = null
    private val transformations =
        HashMap<Class<*>, Transformation<*>>()

    fun getOverrideHeight() = overrideHeight
    fun getOverrideWidth() = overrideWidth
    fun isTransformationSet() = fields.isSet(TRANSFORMATION)
    fun isTransformationAllowed(): Boolean = isTransformationAllowed
    fun getSignature(): Key = signature
    fun getResourceClass(): Class<*> = resourceClass
    fun getDiskCacheStrategy(): DiskCacheStrategy = diskCacheStrategy
    fun getTransformations(): Map<Class<*>, Transformation<*>> = transformations
    fun isTransformationRequired(): Boolean = isTransformationRequired
    fun isScaleOnlyOrNoTransform(): Boolean = isScaleOnlyOrNoTransform
    fun getOptions(): Options = options
    fun isMemoryCacheable(): Boolean = isCacheable
    fun isPrioritySet(): Boolean = fields.isSet(PRIORITY)
    fun getUseUnlimitedSourceGeneratorsPool(): Boolean = useUnlimitedSourceGeneratorsPool
    fun getUseAnimationPool(): Boolean = useAnimationPool
    fun getOnlyRetrieveFromCache(): Boolean = onlyRetrieveFromCache
    fun getSizeMultiplier(): Float = sizeMultiplier
    fun getFallbackDrawable(): Drawable? = fallbackDrawable
    fun getErrorPlaceholder(): Drawable? = errorPlaceholder
    fun getPlaceholderDrawable(): Drawable? = placeholderDrawable
    fun getFallbackId(): Int = fallbackId
    fun getErrorId(): Int = errorId
    fun getPlaceholderId(): Int = placeholderId
    fun getTheme(): Theme? = theme


    fun downsample(strategy: DownsampleStrategy): T {
        return set(DownsampleStrategy.OPTION, strategy)
    }

    open operator fun <Y> set(option: Option<Y>, value: Y): T {
        options[option] = value
        return self()
    }

    private fun self(): T {
        return this as T
    }

    /**
     * 应用其他BaseRequestOptions
     */
    fun  apply(o: BaseRequestOptions<*>): T {
        val other = o
        other.fields.apply {
            if (isSet(SIZE_MULTIPLIER)) {
                sizeMultiplier = other.sizeMultiplier
            }
            if (isSet(USE_UNLIMITED_SOURCE_GENERATORS_POOL)) {
                useUnlimitedSourceGeneratorsPool = other.useUnlimitedSourceGeneratorsPool
            }
            if (isSet(USE_ANIMATION_POOL)) {
                useAnimationPool = other.useAnimationPool
            }
            if (isSet(DISK_CACHE_STRATEGY)) {
                diskCacheStrategy = other.diskCacheStrategy
            }
            if (isSet(PRIORITY)) {
                priority = other.priority
            }
            if (isSet(ERROR_PLACEHOLDER)) {
                errorPlaceholder = other.errorPlaceholder
                errorId = 0
                fields.unSet(ERROR_ID)
            }
            if (isSet(ERROR_ID)) {
                errorId = other.errorId
                errorPlaceholder = null
                fields.unSet(ERROR_PLACEHOLDER)
            }
            if (isSet(PLACEHOLDER)) {
                placeholderDrawable = other.placeholderDrawable
                placeholderId = 0
                fields.unSet(PLACEHOLDER_ID)
            }
            if (isSet(PLACEHOLDER_ID)) {
                placeholderId = other.placeholderId
                placeholderDrawable = null
                fields.unSet(PLACEHOLDER)
            }
            if (isSet(IS_CACHEABLE)) {
                isCacheable = other.isCacheable
            }
            if (isSet(OVERRIDE)) {
                overrideWidth = other.overrideWidth
                overrideHeight = other.overrideHeight
            }
            if (isSet(SIGNATURE)) {
                signature = other.signature
            }
            if (isSet(RESOURCE_CLASS)) {
                resourceClass = other.resourceClass
            }
            if (isSet(FALLBACK)) {
                fallbackDrawable = other.fallbackDrawable
                fallbackId = 0
                fields.unSet(FALLBACK_ID)
            }
            if (isSet(FALLBACK_ID)) {
                fallbackId = other.fallbackId
                fallbackDrawable = null
                fields.unSet(FALLBACK)
            }
            if (isSet(THEME)) {
                theme = other.theme
            }
            if (isSet(TRANSFORMATION_ALLOWED)) {
                isTransformationAllowed = other.isTransformationAllowed
            }
            if (isSet(TRANSFORMATION_REQUIRED)) {
                isTransformationRequired = other.isTransformationRequired
            }
            if (isSet(TRANSFORMATION)) {
                transformations.putAll(other.transformations)
                isScaleOnlyOrNoTransform = other.isScaleOnlyOrNoTransform
            }
            if (isSet(ONLY_RETRIEVE_FROM_CACHE)) {
                onlyRetrieveFromCache = other.onlyRetrieveFromCache
            }
            if (!isTransformationAllowed) {
                transformations.clear()
                fields.unSet(TRANSFORMATION)
                fields.unSet(TRANSFORMATION_REQUIRED)
                isTransformationRequired = false
                isScaleOnlyOrNoTransform = true
            }
        }
        fields = fields or other.fields
        options.putAll(other.options)
        return self()
    }

}