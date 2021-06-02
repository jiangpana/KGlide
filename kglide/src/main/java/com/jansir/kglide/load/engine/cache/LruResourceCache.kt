package com.jansir.kglide.load.engine.cache

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.Resource

class LruResourceCache:MemoryCache {
    override fun getCurrentSize(): Long {
        return 0L
    }

    override fun getMaxSize(): Long {
        return 0L
    }

    override fun setSizeMultiplier(multiplier: Float) {
    }

    override fun remove(key: Key): Resource<*>? {
        return null
    }

    override fun put(key: Key, resource: Resource<*>?): Resource<*>? {
        return null
    }

    override fun setResourceRemovedListener(listener: MemoryCache.ResourceRemovedListener) {
    }

    override fun clearMemory() {
    }

    override fun trimMemory(level: Int) {
    }
}