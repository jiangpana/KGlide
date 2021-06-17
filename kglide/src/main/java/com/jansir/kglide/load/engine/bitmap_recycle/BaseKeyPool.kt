package com.jansir.kglide.load.engine.bitmap_recycle

import java.util.*

abstract class BaseKeyPool <T : Poolable>{
    companion object{
        private const val MAX_SIZE = 20
    }
    private val keyPool = ArrayDeque<T>(MAX_SIZE)

    open fun get(): T {
        var result: T? = keyPool.poll()
        if (result == null) {
            result = create()
        }
        return result
    }
    open fun offer(key: T) {
        if (keyPool.size < MAX_SIZE) {
            keyPool.offer(key)
        }
    }
    abstract fun create(): T
}