package com.jansir.kglide.load.engine.cache

import android.content.Context

class InternalCacheDiskCacheFactory(val context: Context): DiskCache.Factory {
    override fun build(): DiskCache? {
        return null
    }
}