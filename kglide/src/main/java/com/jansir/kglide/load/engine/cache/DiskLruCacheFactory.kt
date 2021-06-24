package com.jansir.kglide.load.engine.cache

import com.jansir.kglide.ext.printThis
import java.io.File

open class DiskLruCacheFactory : DiskCache.Factory {

    private var diskCacheSize: Long = 0
    private var cacheDirectoryGetter: CacheDirectoryGetter

    constructor(diskCacheFolder: String, diskCacheSize: Long) : this(cacheDirectoryGetter = object :
        CacheDirectoryGetter {
        override val cacheDirectory: File
            get() = File(diskCacheFolder)
    }, diskCacheSize = diskCacheSize)

    constructor(diskCacheFolder: String, diskCacheName: String, diskCacheSize: Long)  : this(cacheDirectoryGetter = object :
        CacheDirectoryGetter {
        override val cacheDirectory: File
            get() = File(diskCacheFolder,diskCacheName)
    }, diskCacheSize = diskCacheSize)

    constructor(cacheDirectoryGetter: CacheDirectoryGetter, diskCacheSize: Long = 0) {
        this.cacheDirectoryGetter =cacheDirectoryGetter
        this.diskCacheSize =diskCacheSize
    }

    interface CacheDirectoryGetter {
        val cacheDirectory: File?
    }

    override fun build(): DiskCache? {
        val cacheDir = cacheDirectoryGetter.cacheDirectory ?: return null
        if (!cacheDir.mkdirs() && (!cacheDir.exists() || !cacheDir.isDirectory)) {
            return null
        }
        printThis("${cacheDir.absolutePath}")
        return DiskLruCacheWrapper.create(cacheDir, diskCacheSize)
    }
}