package com.jansir.kglide.load.engine.cache

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.Resource

interface MemoryCache {
    interface ResourceRemovedListener {
        fun onResourceRemoved(removed: Resource<*>)
    }
    fun getCurrentSize(): Long
    fun getMaxSize(): Long
    fun setSizeMultiplier(multiplier: Float)
    fun remove(key: Key): Resource<*>?
    //旧值（如果键不在映射中，则为 null）
    fun put(key: Key, resource: Resource<*>): Resource<*>?
    fun setResourceRemovedListener(listener: ResourceRemovedListener)
    /** Evict all items from the memory cache.  */
    fun clearMemory()
    fun trimMemory(level: Int)

}