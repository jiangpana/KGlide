package com.jansir.kglide.provider

import com.jansir.kglide.load.Encoder
import com.jansir.kglide.load.ResourceEncoder
import java.util.*

class EncoderRegistry {

    private val encoders = ArrayList<Entry<*>>()

    @Synchronized
    fun <Z> append(dataClass: Class<Z>, encoder: Encoder<Z>) {
        encoders.add(Entry(dataClass,encoder))
    }


    fun <X> getEncoder(dataClass: Class<X>): Encoder<X>? {
        encoders.forEach {
            if (it.handles(dataClass)){
                return it.encoder as Encoder<X>
            }
        }
        // TODO: throw an exception here?
        return null
    }

    private class Entry<T>(val resourceClass: Class<T>, val encoder: Encoder<T>) {

        fun handles(resourceClass: Class<*>): Boolean {
            return this.resourceClass.isAssignableFrom(resourceClass)
        }
    }
}