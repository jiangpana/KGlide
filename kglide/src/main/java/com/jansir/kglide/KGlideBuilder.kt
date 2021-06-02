package com.jansir.kglide

import android.content.Context
import com.jansir.kglide.load.engine.Engine
import com.jansir.kglide.load.engine.bitmap_recycle.LruArrayPool
import com.jansir.kglide.load.engine.bitmap_recycle.LruBitmapPool
import com.jansir.kglide.load.engine.cache.DiskCache
import com.jansir.kglide.load.engine.cache.InternalCacheDiskCacheFactory
import com.jansir.kglide.load.engine.cache.LruResourceCache
import com.jansir.kglide.load.engine.cache.MemoryCache
import com.jansir.kglide.load.engine.exector.GlideExecutor
import com.jansir.kglide.manager.ConnectivityMonitorFactory
import com.jansir.kglide.manager.DefaultConnectivityMonitorFactory
import com.jansir.kglide.manager.RequestManagerRetriever
import com.jansir.kglide.request.RequestOptions

internal class KGlideBuilder {

    private val defaultRequestOptionsFactory: KGlide.RequestOptionsFactory =
        object : KGlide.RequestOptionsFactory {
            override fun build(): RequestOptions {
                return RequestOptions()
            }
        }
    var connectivityMonitorFactory: ConnectivityMonitorFactory? = null
    var animationExecutor: GlideExecutor? = null
    var sourceExecutor: GlideExecutor? = null
    var diskCacheExecutor: GlideExecutor? = null
    var memoryCache: MemoryCache? = null
    var diskCacheFactory: DiskCache.Factory? = null
    var engine: Engine? = null
    val isActiveResourceRetentionAllowed = false
    fun build(context: Context): KGlide {
        if (sourceExecutor == null) {
            sourceExecutor = GlideExecutor.newSourceExecutor()
        }
        if (diskCacheExecutor == null) {
            diskCacheExecutor = GlideExecutor.newDiskCacheExecutor()
        }
        if (animationExecutor == null) {
            animationExecutor = GlideExecutor.newAnimationExecutor()
        }
        if (connectivityMonitorFactory == null) {
            connectivityMonitorFactory = DefaultConnectivityMonitorFactory()
        }
        if (memoryCache == null) {
            memoryCache = LruResourceCache()
        }
        if (diskCacheFactory == null) {
            diskCacheFactory = InternalCacheDiskCacheFactory(context)
        }
        if (engine == null) {
            engine = Engine(
                memoryCache!!,
                diskCacheFactory!!,
                diskCacheExecutor!!,
                sourceExecutor!!,
                GlideExecutor.newUnlimitedSourceExecutor(),
                animationExecutor!!,
                isActiveResourceRetentionAllowed = isActiveResourceRetentionAllowed
            )
        }
        val requestManagerRetriever = RequestManagerRetriever()
        val connectivityMonitorFactory = DefaultConnectivityMonitorFactory()
        val bitmapPool = LruBitmapPool()
        val arrayPool = LruArrayPool()
        return KGlide(
            context,
            engine!!,
            memoryCache!!,
            requestManagerRetriever,
            connectivityMonitorFactory,
            bitmapPool,
            arrayPool,
            defaultRequestOptionsFactory

        )
    }


}