package com.jansir.kglide

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    private val UNSET = -1
    private val SIZE_MULTIPLIER = 1 shl 1
    private val DISK_CACHE_STRATEGY = 1 shl 2

    private var fields = 0

    @Test
    fun addition_isCorrect() {
        fields = fields.set(SIZE_MULTIPLIER)
        fields = fields.set(DISK_CACHE_STRATEGY)
        fields = fields.unSet(DISK_CACHE_STRATEGY)
//        fields = fields or SIZE_MULTIPLIER
//        fields = fields or DISK_CACHE_STRATEGY
//        fields = fields and SIZE_MULTIPLIER.inv()
        if (fields.isSet(SIZE_MULTIPLIER)) {
            print("SIZE_MULTIPLIER set ")
        }
        if (fields.isSet(DISK_CACHE_STRATEGY)) {
            print("DISK_CACHE_STRATEGY set ")
        }
    }

    fun Int.isSet(flag: Int): Boolean {
        return this and flag != 0
    }

    fun Int.set(flag: Int): Int {
        return this or flag
    }

    fun Int.unSet(flag: Int): Int {
        return this and flag.inv()
    }
}