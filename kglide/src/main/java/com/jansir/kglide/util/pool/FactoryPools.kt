package com.jansir.kglide.util.pool

import androidx.core.util.Pools
import java.util.ArrayList

object FactoryPools {
    private const val TAG = "FactoryPools"
    private const val DEFAULT_POOL_SIZE = 20

    fun <T> threadSafeList(): Pools.Pool<List<T>> {
        return threadSafeList(DEFAULT_POOL_SIZE)
    }

    fun <T> threadSafeList(size: Int): Pools.Pool<List<T>> {
        return build(size)
    }

    private fun <T> build(size: Int): Pools.Pool<List<T>> {
        val factory = object : Factory<List<T>> {
            override fun create(): List<T> {
                return ArrayList()
            }

        }
        val resetter = object : Resetter<List<T>> {
            override fun reset(setter: List<T>) {

            }
        }
        return build(Pools.SynchronizedPool<List<T>>(size), factory, resetter)
    }

    private fun <T> build(
        pool: Pools.Pool<T>,
        factory: FactoryPools.Factory<T>,
        resetter: Resetter<T>
    ): Pools.Pool<T> {
        return FactoryPool(pool, factory, resetter)
    }

    private class FactoryPool<T>(pool: Pools.Pool<T>, factory: Factory<T>, reSetter: Resetter<T>) :
        Pools.Pool<T> {
        override fun acquire(): T? {
            return null
        }

        override fun release(instance: T): Boolean {
            return true
        }

    }

    interface Factory<T> {
        fun create(): T
    }

    interface Resetter<T> {
        fun reset(setter: T)
    }
}