package com.jansir.kglide.load.engine

import com.jansir.kglide.util.pool.FactoryPools
import com.jansir.kglide.util.pool.StateVerifier


class LockedResource<Z> : Resource<Z>, FactoryPools.Poolable {

    private var toWrap: Resource<Z>? = null
    private var isLocked = false
    private var isRecycled = false
    private val stateVerifier = StateVerifier.newInstance();
    companion object{
      private val POOL by lazy {
            FactoryPools.threadSafe(20 , object : FactoryPools.Factory<LockedResource<*>> {
                override fun create(): LockedResource<*> {
                    return LockedResource<Any>();
                }

            })
        }
        fun <Z> obtain(resource: Resource<Z>):LockedResource<Z> {
            val result = POOL.acquire() as LockedResource<Z>
            result.init(resource )
            return result
        }
    }

    private fun init(toWrap: Resource<Z>) {
        isRecycled = false
        isLocked = true
        this.toWrap = toWrap
    }

    private fun release() {
        toWrap = null
        POOL.release(this)
    }

    override fun getResourceClass(): Class<Z> {
        return toWrap!!.getResourceClass()
    }

    override fun get(): Z {
        return toWrap!!.get()
    }

    override fun getSize(): Int {
        return toWrap!!.getSize()
    }

    override fun recycle() {
        stateVerifier.throwIfRecycled()
        isRecycled =true
        if (!isLocked){
            toWrap!!.recycle()
            release()
        }
    }

    override fun getVerifier(): StateVerifier =stateVerifier
}