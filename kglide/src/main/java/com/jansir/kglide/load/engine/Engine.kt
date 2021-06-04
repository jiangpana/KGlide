package com.jansir.kglide.load.engine

import android.util.Log
import androidx.core.util.Pools
import com.jansir.kglide.load.engine.cache.DiskCache
import com.jansir.kglide.load.engine.cache.MemoryCache
import com.jansir.kglide.load.engine.exector.GlideExecutor

class Engine(
    val cache: MemoryCache,
    val diskCacheFactory: DiskCache.Factory,
    val diskCacheExecutor: GlideExecutor,
    val sourceExecutor: GlideExecutor,
    val sourceUnlimitedExecutor: GlideExecutor,
    val animationExecutor: GlideExecutor,
    var jobs: Jobs?=null,
    var engineKeyFactory: EngineKeyFactory?=null,
    var activeResources: ActiveResources?=null,
    var engineJobFactory: EngineJobFactory?=null,
    var decodeJobFactory: DecodeJobFactory?=null,
    var resourceRecycler: ResourceRecycler?=null,
    isActiveResourceRetentionAllowed :Boolean
) {

    companion object{
        private const val TAG = "Engine"
        private const val JOB_POOL_SIZE = 150
        private val VERBOSE_IS_LOGGABLE =
            Log.isLoggable(TAG, Log.VERBOSE)
    }
    class EngineJobFactory {


    }

    class DecodeJobFactory {

    }
}