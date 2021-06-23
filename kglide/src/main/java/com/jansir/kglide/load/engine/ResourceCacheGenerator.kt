package com.jansir.kglide.load.engine

import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.data.DataFetcher

class ResourceCacheGenerator(val helper:DecodeHelper<*>,
    val callback: DataFetcherGenerator.FetcherReadyCallback
): DataFetcherGenerator, DataFetcher.DataCallback<Any> {
    override fun startNext():Boolean {
        printThis("startNext ->" +Thread.currentThread().name)
        return false
    }

    override fun cancel() {
    }

    override fun onDataReady(data: Any) {
    }

    override fun onLoadFailed(e: Exception) {
    }
}