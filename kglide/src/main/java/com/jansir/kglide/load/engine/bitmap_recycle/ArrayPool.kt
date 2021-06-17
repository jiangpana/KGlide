package com.jansir.kglide.load.engine.bitmap_recycle

interface ArrayPool {

    companion object{
        //64k
       const val STANDARD_BUFFER_SIZE_BYTES = 64 * 1024
    }
    @Deprecated("Use {@link #put(Object)}")
    fun <T: Any> put(array: T, arrayClass: Class<T>)
    fun <T: Any> put(array: T)
    operator fun <T: Any> get(size: Int, arrayClass: Class<T>): T
    fun <T: Any> getExact(size: Int, arrayClass: Class<T>): T
    fun clearMemory()
    fun trimMemory(level: Int)
}