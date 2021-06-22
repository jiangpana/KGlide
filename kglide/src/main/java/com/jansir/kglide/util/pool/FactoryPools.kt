package com.jansir.kglide.util.pool

import androidx.core.util.Pools
import androidx.core.util.Pools.SynchronizedPool
import java.util.*

object FactoryPools {
    private const val TAG = "FactoryPools"
    private const val DEFAULT_POOL_SIZE = 20

    private fun <T>emptyResetter()=Resetter.EMPTY_RESETTER  as Resetter<T>

    fun <T> threadSafeList(): Pools.Pool<MutableList<T>> {
        return threadSafeList(DEFAULT_POOL_SIZE)
    }

    fun <T> threadSafeList(size: Int): Pools.Pool<MutableList<T>> {
        val factory = object : Factory<MutableList<T>> {
            override fun create(): MutableList<T> {
                return ArrayList()
            }

        }
        val resetter = object : Resetter<MutableList<T>> {
            override fun reset(setter: MutableList<T>) {
                setter.clear()
            }
        }
        return build(SynchronizedPool<MutableList<T>>(size),factory, resetter)
    }

    fun <T> simple(size:Int,factory:Factory<T>): Pools.Pool<T> {
        return build(Pools.SimplePool(size),factory)
    }
    fun <T> threadSafe(size:Int ,factory : Factory<T> ): Pools.Pool<T> {
        return build(SynchronizedPool<T>(size), factory)
    }

    private fun <T> build(pool: Pools.Pool<T>,factory: Factory<T>): Pools.Pool<T>{
        return build(pool, factory, emptyResetter() )
    }

    private fun <T> build(
        pool: Pools.Pool<T>,
        factory: Factory<T>,
        resetter: Resetter<T>
    ): Pools.Pool<T> {
        return FactoryPool(pool, factory, resetter)
    }

    private class FactoryPool<T>(
        val pool: Pools.Pool<T>,
        val factory: Factory<T>,
        val resetter: Resetter<T>
    ) :
        Pools.Pool<T> {
        override fun acquire(): T {
            var result = pool.acquire()
            if (result == null) {
                result = factory.create()
            }
            if (result is Poolable){
                result.getVerifier().setRecycled(false)
            }
            return result!!
        }

        override fun release(instance: T): Boolean {
            if (instance is Poolable){
                instance.getVerifier().setRecycled(true)
            }
            resetter.reset(instance)
            return pool.release(instance)
        }

    }

    interface Factory<T> {
        fun create(): T
    }


    interface Resetter<T> {
        companion object {
            val EMPTY_RESETTER = object : Resetter<Any> {
                override fun reset(setter: Any) {}
            }
        }

        fun reset(setter: T)
    }

    interface Poolable{
        fun getVerifier(): StateVerifier
    }
}