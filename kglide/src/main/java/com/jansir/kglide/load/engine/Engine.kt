package com.jansir.kglide.load.engine

import android.util.Log
import androidx.core.util.Pools
import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.engine.cache.DiskCache
import com.jansir.kglide.load.engine.cache.MemoryCache
import com.jansir.kglide.load.engine.exector.GlideExecutor
import com.jansir.kglide.load.key.EngineJobListener
import com.jansir.kglide.request.ResourceCallback
import com.jansir.kglide.util.pool.FactoryPools
import java.util.concurrent.Executor

class Engine(
    val cache: MemoryCache,
    val diskCacheFactory: DiskCache.Factory,
    val diskCacheExecutor: GlideExecutor,
    val sourceExecutor: GlideExecutor,
    val sourceUnlimitedExecutor: GlideExecutor,
    val animationExecutor: GlideExecutor,
    var jobs: Jobs = Jobs(),
    var engineKeyFactory: EngineKeyFactory = EngineKeyFactory(),
    var activeResources: ActiveResources? = null,
    var engineJobFactory: EngineJobFactory? = null,
    var decodeJobFactory: DecodeJobFactory? = null,
    var resourceRecycler: ResourceRecycler? = null,
    isActiveResourceRetentionAllowed: Boolean
) : EngineJobListener, EngineResource.ResourceListener {

    init {
        if (decodeJobFactory == null) {
            decodeJobFactory = DecodeJobFactory(
                diskCacheProvider = LazyDiskCacheProvider(
                    diskCacheFactory.build()
                )
            )
        }
        if (engineJobFactory == null) {
            engineJobFactory = EngineJobFactory(
                diskCacheExecutor,
                sourceExecutor,
                sourceUnlimitedExecutor,
                animationExecutor,
                this,
                this
            )
        }

    }

    fun <R> load(
        glideContext: GlideContext,
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        resourceClass: Class<*>,
        transcodeClass: Class<R>,
        priority: Priority,
        diskCacheStrategy: DiskCacheStrategy,
        transformations: Map<Class<*>, Transformation<*>>,
        isTransformationRequired: Boolean,
        isScaleOnlyOrNoTransform: Boolean,
        options: Options,
        isMemoryCacheable: Boolean,
        useUnlimitedSourceExecutorPool: Boolean,
        useAnimationPool: Boolean,
        onlyRetrieveFromCache: Boolean,
        cb: ResourceCallback,
        callbackExecutor: Executor
    ): LoadStatus? {
        val key = engineKeyFactory.buildKey(
            model,
            signature,
            width,
            height,
            transformations,
            resourceClass,
            transcodeClass, options
        )
        var memoryResource: EngineResource<*>? = null
        synchronized(this) {
            val startTime = 0L
            memoryResource = loadFromMemory(key, isMemoryCacheable, startTime)
            if (memoryResource == null) {
                return waitForExistingOrStartNewJob(
                    glideContext,
                    model,
                    signature,
                    width,
                    height,
                    resourceClass,
                    transcodeClass,
                    priority,
                    diskCacheStrategy,
                    transformations,
                    isTransformationRequired,
                    isScaleOnlyOrNoTransform,
                    options,
                    isMemoryCacheable,
                    useUnlimitedSourceExecutorPool,
                    useAnimationPool,
                    onlyRetrieveFromCache,
                    cb,
                    callbackExecutor,
                    key,
                    startTime
                )
            }
        }
        cb.onResourceReady(memoryResource!!, DataSource.MEMORY_CACHE)
        return null
    }

    private fun <R> waitForExistingOrStartNewJob(
        glideContext: GlideContext,
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        resourceClass: Class<*>,
        transcodeClass: Class<R>,
        priority: Priority,
        diskCacheStrategy: DiskCacheStrategy,
        transformations: Map<Class<*>, Transformation<*>>,
        isTransformationRequired: Boolean,
        isScaleOnlyOrNoTransform: Boolean,
        options: Options,
        isMemoryCacheable: Boolean,
        useUnlimitedSourceExecutorPool: Boolean,
        useAnimationPool: Boolean,
        onlyRetrieveFromCache: Boolean,
        cb: ResourceCallback,
        callbackExecutor: Executor,
        key: EngineKey,
        startTime: Long
    ): LoadStatus? {
        val current: EngineJob<*>? = jobs.get(key, onlyRetrieveFromCache)
        current?.let {
            current.addCallback(cb, callbackExecutor)
            return LoadStatus(current, cb)
        }
        val engineJob = engineJobFactory!!.build<R>(
            key,
            isMemoryCacheable,
            useUnlimitedSourceExecutorPool,
            useAnimationPool,
            onlyRetrieveFromCache
        )
        val decodeJob = decodeJobFactory!!.build(
            glideContext,
            model,
            key,
            signature,
            width,
            height,
            resourceClass,
            transcodeClass,
            priority,
            diskCacheStrategy,
            transformations,
            isTransformationRequired,
            isScaleOnlyOrNoTransform,
            onlyRetrieveFromCache,
            options,
            engineJob
        )
        jobs.put(key, engineJob)
        engineJob.addCallback(cb, callbackExecutor)
        engineJob.start(decodeJob)
        return LoadStatus(engineJob, cb)
    }

    private fun loadFromMemory(
        key: EngineKey,
        isMemoryCacheable: Boolean,
        startTime: Long
    ): EngineResource<*>? {
        if (!isMemoryCacheable) {
            return null
        }
        return null
    }

    companion object {
        private const val TAG = "Engine"
        private const val JOB_POOL_SIZE = 150
        private val VERBOSE_IS_LOGGABLE =
            Log.isLoggable(TAG, Log.VERBOSE)


    }

    override fun onEngineJobComplete(
        engineJob: EngineJob<*>?,
        key: Key?,
        resource: EngineResource<*>?
    ) {
    }

    override fun onEngineJobCancelled(engineJob: EngineJob<*>?, key: Key?) {
    }

    override fun onResourceReleased(key: Key?, resource: EngineResource<*>?) {
    }

    class EngineJobFactory(
        val diskCacheExecutor: GlideExecutor,
        val sourceExecutor: GlideExecutor,
        val sourceUnlimitedExecutor: GlideExecutor,
        val animationExecutor: GlideExecutor,
        val engineJobListener: EngineJobListener,
        val resourceListener: EngineResource.ResourceListener
    ) {

        lateinit var pool: Pools.Pool<EngineJob<*>>

        init {
            pool = FactoryPools.threadSafe(JOB_POOL_SIZE,
                object : FactoryPools.Factory<EngineJob<*>> {
                    override fun create(): EngineJob<*> {
                        return EngineJob<Any>(
                            diskCacheExecutor,
                            sourceExecutor,
                            sourceUnlimitedExecutor,
                            animationExecutor,
                            engineJobListener,
                            resourceListener,
                            pool
                        )
                    }
                })
        }


        fun <R> build(
            key: Key,
            isMemoryCacheable: Boolean,
            useUnlimitedSourceGeneratorPool: Boolean,
            useAnimationPool: Boolean,
            onlyRetrieveFromCache: Boolean
        ): EngineJob<R> {
            var result = pool.acquire() as EngineJob<R>
            return result.init(
                key,
                isMemoryCacheable,
                useUnlimitedSourceGeneratorPool,
                useAnimationPool,
                onlyRetrieveFromCache
            )
        }

    }

    class DecodeJobFactory(val diskCacheProvider: DecodeJob.DiskCacheProvider) {
        lateinit var pool: Pools.Pool<DecodeJob<*>>

        init {
            pool = FactoryPools.threadSafe(JOB_POOL_SIZE,
                object : FactoryPools.Factory<DecodeJob<*>> {
                    override fun create(): DecodeJob<*> {
                        return DecodeJob<Any>(diskCacheProvider, pool)
                    }
                })
        }

        private var creationOrder = 0
        fun <R> build(
            glideContext: GlideContext,
            model: Any,
            loadKey: EngineKey,
            signature: Key,
            width: Int,
            height: Int,
            resourceClass: Class<*>,
            transcodeClass: Class<R>,
            priority: Priority,
            diskCacheStrategy: DiskCacheStrategy,
            transformations: Map<Class<*>, Transformation<*>>,
            isTransformationRequired: Boolean,
            isScaleOnlyOrNoTransform: Boolean,
            onlyRetrieveFromCache: Boolean,
            options: Options,
            callback: DecodeJob.Callback<R>
        ): DecodeJob<R> {
            val result: DecodeJob<R> = pool.acquire() as DecodeJob<R>
            return result.init(
                glideContext,
                model,
                loadKey,
                signature,
                width,
                height,
                resourceClass,
                transcodeClass,
                priority,
                diskCacheStrategy,
                transformations,
                isTransformationRequired,
                isScaleOnlyOrNoTransform,
                onlyRetrieveFromCache,
                options,
                callback,
                creationOrder++
            )
        }

    }

    private var loadStatus: LoadStatus? = null

    inner class LoadStatus(val engineJob: EngineJob<*>, val cb: ResourceCallback) {

        fun cancel() {
            synchronized(this@Engine) {
                engineJob.removeCallback(cb)
            }
        }
    }

    private class LazyDiskCacheProvider(override val diskCache: DiskCache) :
        DecodeJob.DiskCacheProvider {


    }
}