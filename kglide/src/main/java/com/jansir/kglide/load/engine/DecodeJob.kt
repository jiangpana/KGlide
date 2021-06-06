package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.engine.cache.DiskCache

class DecodeJob<R>(
    val diskCacheProvider: DiskCacheProvider,
    val pool: Pools.Pool<DecodeJob<*>>
) : Runnable {
    private var glideContext: GlideContext? = null
    private var signature: Key? = null
    private var priority: Priority? = null
    private var loadKey: EngineKey? = null
    private var width = 0
    private var height = 0
    private var diskCacheStrategy: DiskCacheStrategy? = null
    private var options: Options? = null
    private var callback: DecodeJob.Callback<R>? = null
    private var order = 0
    private var stage: DecodeJob.Stage? = null
    private var runReason: DecodeJob.RunReason? = null
    private var startFetchTime: Long = 0
    private var onlyRetrieveFromCache = false
    private var model: Any? = null
    override fun run() {
    }

    interface DiskCacheProvider {
        val diskCache: DiskCache
    }

    interface Callback<R> {
        fun onResourceReady(resource: Resource<R>?, dataSource: DataSource?)
        fun onLoadFailed(e: Exception?)
        fun reschedule(job: DecodeJob<*>?)
    }

    /** Where we're trying to decode data from.  */
    private enum class Stage {
        INITIALIZE,
        RESOURCE_CACHE,
        DATA_CACHE,
        SOURCE,
        ENCODE,
        FINISHED
    }


    private enum class RunReason {
        INITIALIZE,
        SWITCH_TO_SOURCE_SERVICE,
        DECODE_DATA
    }

    fun init(
        glideContext: GlideContext,
        model: Any,
        loadKey: EngineKey,
        signature: Key,
        width: Int,
        height: Int,
        resourceClass: Class<*>?,
        transcodeClass: Class<R>?,
        priority: Priority,
        diskCacheStrategy: DiskCacheStrategy,
        transformations: Map<Class<*>, Transformation<*>>,
        isTransformationRequired: Boolean,
        isScaleOnlyOrNoTransform: Boolean,
        onlyRetrieveFromCache: Boolean,
        options: Options,
        callback: DecodeJob.Callback<R>,
        order: Int
    ): DecodeJob<R> {
        /*      decodeHelper.init(
                  glideContext,
                  model,
                  signature,
                  width,
                  height,
                  diskCacheStrategy,
                  resourceClass,
                  transcodeClass,
                  priority,
                  options,
                  transformations,
                  isTransformationRequired,
                  isScaleOnlyOrNoTransform,
                  diskCacheProvider
              )*/
        this.glideContext = glideContext
        this.signature = signature
        this.priority = priority
        this.loadKey = loadKey
        this.width = width
        this.height = height
        this.diskCacheStrategy = diskCacheStrategy
        this.onlyRetrieveFromCache = onlyRetrieveFromCache
        this.options = options
        this.callback = callback
        this.order = order
        this.runReason = DecodeJob.RunReason.INITIALIZE
        this.model = model
        return this
    }

    fun willDecodeFromCache(): Boolean {
        return true
    }

    fun cancel() {

    }

    fun release(b: Boolean) {

    }
}