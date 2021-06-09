package com.jansir.kglide.load.data

import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource

interface DataFetcher<T> {
    interface DataCallback<T>{
        fun onDataReady(data: T)
        fun onLoadFailed(e: Exception)
    }



    fun loadData(priority:Priority,callback:DataCallback<in T>)
    fun cleanup()
    fun cancel()
    fun getDataClass(): Class<T>
    fun getDataSource(): DataSource

}