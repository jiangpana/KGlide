package com.jansir.kglide.load.engine

import com.jansir.kglide.load.Key


class EngineResource<Z>(
    val toWrap: Resource<Z>,
    val isMemoryCacheable: Boolean,
    val isRecyclable: Boolean,
    val key: Key,
    val listener: ResourceListener
) : Resource<Z> {

    interface ResourceListener {
        fun onResourceReleased(key: Key?, resource: EngineResource<*>?)
    }

    override fun getResourceClass(): Class<Z> {
        return toWrap.getResourceClass()

    }

    override fun get(): Z {
        return toWrap.get()
    }

    override fun getSize(): Int {
        return toWrap.getSize()

    }

    override fun recycle() {
        isRecycled=true
    }

    private var acquired = 0
    private var isRecycled = false
    @Synchronized
    fun acquire() {
        check(!isRecycled) { "Cannot acquire a recycled resource" }
        ++acquired
    }
}