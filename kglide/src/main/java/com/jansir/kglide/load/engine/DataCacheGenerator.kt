package com.jansir.kglide.load.engine

import com.jansir.kglide.load.data.DataFetcher

class DataCacheGenerator(val helper:DecodeHelper<*>,
                         val callback: DataFetcherGenerator.FetcherReadyCallback): DataFetcherGenerator, DataFetcher.DataCallback<Any> {
    override fun startNext() :Boolean{
        return true
    }

    override fun cancel() {
    }

    override fun onDataReady(data: Any) {
    }

    override fun onLoadFailed(e: Exception) {
    }
}