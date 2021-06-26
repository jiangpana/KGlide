package com.jansir.kglide.util

import androidx.collection.ArrayMap
import androidx.collection.SimpleArrayMap


class CachedHashCodeArrayMap <K, V>() : ArrayMap<K, V>() {

    private var hashCode = 0
    override fun clear() {
        hashCode = 0
        super.clear()
    }
    override fun setValueAt(index: Int, value: V): V {
        hashCode = 0
        return super.setValueAt(index, value)
    }

    override fun put(key: K, value: V): V ?{
        hashCode = 0
        return super.put(key, value)
    }

    override fun putAll(simpleArrayMap: SimpleArrayMap<out K, out V>) {
        hashCode = 0
        super.putAll(simpleArrayMap)
    }

    override fun removeAt(index: Int): V {
        hashCode = 0
        return super.removeAt(index)
    }

    override fun hashCode(): Int {
        if (hashCode == 0) {
            hashCode = super.hashCode()
        }
        return hashCode
    }


}