package com.jansir.kglide.load.data

interface DataRewinder<T> {
    interface Factory<T>{
        fun build(data :T)
        fun getDataClass(): Class<T>
    }
    fun rewindAndGet():T
    fun cleanup()
}