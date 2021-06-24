package com.jansir.kglide.load.engine.cache

import android.content.Context
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.cache.DiskCache.Factory.Companion.DEFAULT_DISK_CACHE_DIR
import com.jansir.kglide.load.engine.cache.DiskCache.Factory.Companion.DEFAULT_DISK_CACHE_SIZE
import java.io.File

class InternalCacheDiskCacheFactory(
    context: Context,
    diskCacheName: String = DEFAULT_DISK_CACHE_DIR,
    diskCacheSize: Long = DEFAULT_DISK_CACHE_SIZE.toLong()
) : DiskLruCacheFactory(object : CacheDirectoryGetter {
    override val cacheDirectory: File?
        get() {
            val cacheDirectory = context.cacheDir ?: return null;
            if (diskCacheName.isNotBlank()) {
                return File(cacheDirectory, diskCacheName)
            }
            return cacheDirectory
        }

}, diskCacheSize) {
}