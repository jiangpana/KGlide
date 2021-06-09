package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.data.DataFetcher
import com.jansir.kglide.load.engine.cache.DiskCache

class DecodeJob<R>(
    val diskCacheProvider: DiskCacheProvider,
    val pool: Pools.Pool<DecodeJob<*>>
) : Runnable, Comparable<DecodeJob<*>>, DataFetcherGenerator.FetcherReadyCallback {
    private var glideContext: GlideContext? = null
    private var signature: Key? = null
    private lateinit var priority: Priority
    private var loadKey: EngineKey? = null
    private var width = 0
    private var height = 0
    private lateinit var diskCacheStrategy: DiskCacheStrategy
    private var options: Options? = null
    private var callback: DecodeJob.Callback<R>? = null
    private var order = 0
    private lateinit var stage: Stage
    private var runReason = RunReason.INITIALIZE;
    private var startFetchTime: Long = 0
    private var onlyRetrieveFromCache = false
    private var model: Any? = null

    override fun run() {
        println("DecodeJob #run . thread name ->${Thread.currentThread().name}")
        val localFetcher = currentFetcher
        try {
            if (isCancelled) {
                notifyFailed()
                return
            }
            runWrapped()
        } catch (e: Exception) {
            throw e
        } catch (t: Throwable) {
            if (stage != Stage.ENCODE) {
                notifyFailed()
            }
            if (!isCancelled) {
                throw t
            }
        } finally {
            localFetcher?.cleanup()
        }
    }

    private fun runWrapped() {
        when (runReason) {
            RunReason.INITIALIZE -> {
                stage = getNextStage(Stage.INITIALIZE)
                currentGenerator = getNextGenerator()
                runGenerators()
            }
            RunReason.SWITCH_TO_SOURCE_SERVICE -> {

            }
            RunReason.DECODE_DATA -> {

            }
        }
    }

    private fun runGenerators() {
        var isStarted = false
        while (!isCancelled && currentGenerator != null && !(currentGenerator!!.startNext()
                .apply { isStarted = this })
        ) {
            stage = getNextStage(stage)
            currentGenerator = getNextGenerator()
            if (stage == Stage.SOURCE) {
                reschedule()
                return
            }
        }
        if ((stage==Stage.FINISHED || isCancelled)&&!isStarted){
            notifyFailed()
        }
    }

    private val decodeHelper: DecodeHelper<R> = DecodeHelper()

    private fun getNextGenerator(): DataFetcherGenerator? {
        return when (stage) {
            Stage.RESOURCE_CACHE -> {
                ResourceCacheGenerator(decodeHelper, this)
            }
            Stage.DATA_CACHE -> {
                DataCacheGenerator(decodeHelper, this)
            }
            Stage.SOURCE -> {
                SourceGenerator(decodeHelper, this)
            }
            Stage.FINISHED -> {
                null
            }
            else -> throw IllegalStateException("Unrecognized stage: $stage")
        }
    }

    private fun getNextStage(current: Stage): Stage {
        return when (current) {
            Stage.INITIALIZE -> {
                if (diskCacheStrategy.decodeCachedResource()) Stage.RESOURCE_CACHE else getNextStage(
                    Stage.RESOURCE_CACHE
                )
            }
            Stage.RESOURCE_CACHE -> {
                if (diskCacheStrategy.decodeCachedData()) Stage.DATA_CACHE else getNextStage(
                    Stage.DATA_CACHE
                )
            }
            Stage.DATA_CACHE -> {
                return if (onlyRetrieveFromCache) Stage.FINISHED else Stage.SOURCE
            }
            Stage.SOURCE, Stage.FINISHED -> {
                return Stage.FINISHED
            }
            else -> {
                throw IllegalArgumentException("Unrecognized stage: $current")
            }
        }
    }

    private fun notifyFailed() {
        callback!!.onLoadFailed(java.lang.Exception(""))
        onLoadFailed()
    }

    private fun onLoadFailed() {

    }

    private var currentFetcher: DataFetcher<*>? = null

    @Volatile
    private var currentGenerator: DataFetcherGenerator? = null

    @Volatile
    private var isCancelled = false

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
        resourceClass: Class<*>,
        transcodeClass: Class<R>,
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
              decodeHelper.init(
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
              )
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
        this.runReason = RunReason.INITIALIZE
        this.model = model
        return this
    }

    fun willDecodeFromCache(): Boolean {
        return true
    }

    fun cancel() {
        isCancelled = true
        val local = currentGenerator
        local?.cancel()
    }

    fun release(b: Boolean) {

    }

    override fun compareTo(other: DecodeJob<*>): Int {
        var result: Int = priority.ordinal - other.priority.ordinal
        if (result == 0) {
            result = order - other.order
        }
        return result
    }


    override fun reschedule() {
    }

    override fun onDataFetcherReady(
        sourceKey: Key,
        data: Any,
        fetcher: DataFetcher<*>,
        dataSource: DataSource?,
        attemptedKey: Key
    ) {
    }

    override fun onDataFetcherFailed(
        attemptedKey: Key,
        e: Exception?,
        fetcher: DataFetcher<*>,
        dataSource: DataSource
    ) {
    }
}