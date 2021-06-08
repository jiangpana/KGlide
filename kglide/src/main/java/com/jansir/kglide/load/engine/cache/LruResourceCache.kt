package com.jansir.kglide.load.engine.cache

import android.content.ComponentCallbacks2
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.util.LruCache

class LruResourceCache(val size: Long) : LruCache<Key, Resource<*>>(size), MemoryCache {

    private  var listener: MemoryCache.ResourceRemovedListener?=null

    override fun setResourceRemovedListener(listener: MemoryCache.ResourceRemovedListener) {
        this.listener = listener
    }

    override fun onItemEvicted(key: Key, toRemove: Resource<*>) {
        super.onItemEvicted(key, toRemove)
        listener?.onResourceRemoved(toRemove)
    }

    override fun trimMemory(level: Int) {
        if (level >= ComponentCallbacks2.TRIM_MEMORY_BACKGROUND) {
            // Entering list of cached background apps
            // Evict our entire bitmap cache
            super.clearMemory()
        } else if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN
            || level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL
        ) {
            // The app's UI is no longer visible, or app is in the foreground but system is running
            // critically low on memory
            // Evict oldest half of our bitmap cache
            super.trimToSize(super.getMaxSize() / 2)
        }
    }

    override fun getSize(item: Resource<*>?): Int {
        return item?.getSize() ?: super.getSize(null)
    }
}