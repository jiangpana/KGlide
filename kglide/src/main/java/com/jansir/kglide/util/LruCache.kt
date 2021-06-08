package com.jansir.kglide.util

import kotlin.math.round
import kotlin.math.roundToLong

open class LruCache<T, Y>(size: Long) {

    private val cache = LinkedHashMap<T, Y>(100, 0.75f, true)
    private var initialMaxSize = 0L
    private var maxSize = 0L
    private var currentSize = 0L

    init {
        initialMaxSize = size
        maxSize = size
    }

    open fun setSizeMultiplier(multiplier: Float) {
        require(multiplier >= 0) { "Multiplier must be >= 0" }
        maxSize = round(initialMaxSize * multiplier).roundToLong()
        evict()
    }

    private fun evict() {
        trimToSize(maxSize)
    }

    open fun clearMemory() {
        trimToSize(0)
    }

    @Synchronized
    open  fun put(key :T ,item:Y):Y?{
        val itemSize= getSize(item);
        if (itemSize>maxSize){
            onItemEvicted(key,item)
            return null
        }
        if (item != null) {
            //item 不为空把currentSize +1
            currentSize += itemSize.toLong()
        }
        val old = cache.put(key, item)
       //有old值, 把currentSize -1 ,并调用onItemEvicted
        old?.let {
            currentSize -= getSize(old).toLong()
            if (old != item) {
                onItemEvicted(key, old)
            }
        }
        evict()
        return old
    }
    @Synchronized
    protected fun trimToSize(maxSize: Long) {
        var last: Map.Entry<T, Y>
        var cacheIterator: Iterator<Map.Entry<T, Y>>
        while (currentSize > maxSize) {
            cacheIterator = cache.entries.iterator()
            last = cacheIterator.next()
            val toRemove = last.value
            val key = last.key
            currentSize -= getSize(toRemove);
            cacheIterator.remove()
            onItemEvicted(key, toRemove)
        }
    }

    protected open fun onItemEvicted(key: T, toRemove: Y) {

    }

    protected open fun getSize(item :Y ?):Int{
        return 1
    }

    @Synchronized
    protected open fun getCount(): Int {
        return cache.size
    }

    @Synchronized
    open fun getMaxSize(): Long {
        return maxSize
    }

    @Synchronized
    open fun getCurrentSize(): Long {
        return currentSize
    }

    @Synchronized
    open operator fun contains(key: T): Boolean {
        return cache.containsKey(key)
    }

    @Synchronized
    open operator fun get(key: T): Y? {
        return cache[key]
    }

    @Synchronized
    open fun remove(key: T): Y? {
        val value = cache.remove(key)
        if (value != null) {
            currentSize -= getSize(value).toLong()
        }
        return value
    }


}