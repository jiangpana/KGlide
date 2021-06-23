package com.jansir.kglide.provider

import com.jansir.kglide.load.ResourceEncoder
import java.util.*

class ResourceEncoderRegistry {

    private val encoders = ArrayList<Entry<*>>()

    @Synchronized
    fun <Z> append(resourceClass: Class<Z>, encoder: ResourceEncoder<Z>) {
        encoders.add(Entry(resourceClass,encoder))
    }

    fun <Z>get(resourceClass:Class<Z>):ResourceEncoder<Z>?{
        encoders.forEach {
            if (it.handles(resourceClass)){
                return it.encoder as ResourceEncoder<Z>
            }
        }
        // TODO: throw an exception here?
        return null
    }

    private class Entry<T>(val resourceClass: Class<T>, val encoder: ResourceEncoder<T>) {

        fun handles(resourceClass: Class<*>): Boolean {
            return this.resourceClass.isAssignableFrom(resourceClass)
        }
    }
}