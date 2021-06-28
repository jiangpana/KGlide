package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.exector.GlideExecutor
import com.jansir.kglide.load.key.EngineJobListener
import com.jansir.kglide.request.ResourceCallback
import com.jansir.kglide.util.Executors
import java.util.*
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

class EngineJob<R>(
    val diskCacheExecutor: GlideExecutor,
    val sourceExecutor: GlideExecutor,
    val sourceUnlimitedExecutor: GlideExecutor,
    val animationExecutor: GlideExecutor,
    val engineJobListener: EngineJobListener,
    val resourceListener: EngineResource.ResourceListener,
    val pool: Pools.Pool<EngineJob<*>>
) : DecodeJob.Callback<R> {

    private var decodeJob: DecodeJob<R>? = null
    private var engineResource: EngineResource<*>? = null
    private var resource: Resource<*>? = null
    private var dataSource: DataSource? = null
    private lateinit var isCacheable: Any
    private var key: Key? = null
    private var useUnlimitedSourceGeneratorPool: Boolean = false
    private var useAnimationPool: Boolean = false
    private var onlyRetrieveFromCache: Boolean = false
    val cbs = ResourceCallbacksAndExecutors()
    private var hasLoadFailed = false
    private var hasResource = false
    private var isCancelled = false
    private val pendingCallbacks = AtomicInteger()

    @Synchronized
    fun removeCallback(cb: ResourceCallback) {
        cbs.remove(cb)
        if (cbs.isEmpty()) {
            cancel()
            val isFinishedRunning = hasResource || hasLoadFailed
            if (isFinishedRunning && pendingCallbacks.get() == 0) {
                release()
            }
        }
    }

    private fun release() {
        requireNotNull(key)
        cbs.clear()
        key = null
        engineResource = null
        resource = null
        hasLoadFailed = false
        isCancelled = false
        hasResource = false
        decodeJob?.release( /*isRemovedFromQueue=*/false)
        decodeJob = null
        dataSource = null
        pool.release(this)
    }

    private fun cancel() {
        if (isDone()) return
        isCancelled = true
        decodeJob?.cancel()
        engineJobListener.onEngineJobCancelled(this, key)
    }

    private fun isDone(): Boolean {
        return hasLoadFailed || hasResource || isCancelled
    }

    fun addCallback(cb: ResourceCallback, callbackExecutor: Executor) {
        cbs.add(cb, callbackExecutor)
    }

    fun init(
        key: Key,
        isCacheable: Boolean,
        useUnlimitedSourceGeneratorPool: Boolean,
        useAnimationPool: Boolean,
        onlyRetrieveFromCache: Boolean
    ): EngineJob<R> {
        this.key = key
        this.isCacheable = isCacheable
        this.useUnlimitedSourceGeneratorPool = useUnlimitedSourceGeneratorPool
        this.useAnimationPool = useAnimationPool
        this.onlyRetrieveFromCache = onlyRetrieveFromCache
        return this
    }

    fun onlyRetrieveFromCache(): Boolean {
        return true
    }

    override fun onResourceReady(resource: Resource<R>?, dataSource: DataSource?) {
        printThis(" onResourceReady")
        synchronized(this) {
            this.resource = resource
            this.dataSource = dataSource
        }
        notifyCallbacksOfResult()
    }

    private fun notifyCallbacksOfResult() {
        val copy = cbs.copy()
        copy.forEach {
            it.executor.execute(CallResourceReady(it.cb))
        }
    }

    override fun onLoadFailed(e: Exception?) {
    }

    override fun reschedule(job: DecodeJob<*>?) {
        getActiveSourceExecutor().execute(job)
    }

    fun start(decodeJob: DecodeJob<R>) {
        this.decodeJob = decodeJob
        val executor: GlideExecutor =
            if (decodeJob.willDecodeFromCache()) diskCacheExecutor else getActiveSourceExecutor()
        executor.execute(decodeJob)
    }

    private fun getActiveSourceExecutor(): GlideExecutor {
        return if (useUnlimitedSourceGeneratorPool) sourceUnlimitedExecutor else if (useAnimationPool) animationExecutor else sourceExecutor
    }

    class ResourceCallbacksAndExecutors(
        val callbacksAndExecutors: MutableList<ResourceCallbackAndExecutor> = ArrayList(2)
    ) : Iterable<ResourceCallbackAndExecutor> {

        override fun iterator(): Iterator<ResourceCallbackAndExecutor> {
            return callbacksAndExecutors.iterator()
        }

        operator fun contains(cb: ResourceCallback): Boolean {
            return callbacksAndExecutors.contains(
                defaultCallbackAndExecutor(cb)
            )
        }

        fun isEmpty(): Boolean {
            return callbacksAndExecutors.isEmpty()
        }

        fun size(): Int {
            return callbacksAndExecutors.size
        }

        fun clear() {
            callbacksAndExecutors.clear()
        }

        fun add(cb: ResourceCallback, executor: Executor) {
            callbacksAndExecutors.add(ResourceCallbackAndExecutor(cb, executor))
        }

        fun remove(cb: ResourceCallback) {
            callbacksAndExecutors.remove(
                defaultCallbackAndExecutor(cb)
            )
        }

        private fun defaultCallbackAndExecutor(cb: ResourceCallback): ResourceCallbackAndExecutor? {
            return ResourceCallbackAndExecutor(cb, Executors.directExecutor())
        }

        fun copy(): ResourceCallbacksAndExecutors =
            ResourceCallbacksAndExecutors(ArrayList(callbacksAndExecutors));
    }

    class ResourceCallbackAndExecutor(val cb: ResourceCallback, val executor: Executor) {

        override fun equals(other: Any?): Boolean {
            if (other is ResourceCallbackAndExecutor) {
                return cb == other.cb
            }
            return false
        }

        override fun hashCode(): Int {
            return cb.hashCode()
        }
    }

    fun callCallbackOnResourceReady(cb: ResourceCallback) {
        try {
            cb.onResourceReady(engineResource!!, dataSource)
        } catch (t: Throwable) {
            throw t
        }
    }

    inner class CallResourceReady(val cb: ResourceCallback) : Runnable {
        override fun run() {
            synchronized(cb.getLock()) {
                synchronized(this@EngineJob) {
                    if (cbs.contains(cb)) {
                        // Acquire for this particular callback.
                        engineResource?.acquire()
                        callCallbackOnResourceReady(cb)
                        removeCallback(cb)
                    }
                }
            }
        }

    }
}