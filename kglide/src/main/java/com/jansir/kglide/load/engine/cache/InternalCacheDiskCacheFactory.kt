package com.jansir.kglide.load.engine.cache

import android.content.Context
import com.jansir.kglide.load.Key
import java.io.File

class InternalCacheDiskCacheFactory(val context: Context): DiskCache.Factory {
    override fun build(): DiskCache {
        return object :DiskCache{
            override fun get(key: Key): File? {
                return null
            }

            override fun put(key: Key?, writer: DiskCache.Writer) {
            }

            override fun delete(key: Key) {
            }

            override fun clear() {
            }
        }
    }
}