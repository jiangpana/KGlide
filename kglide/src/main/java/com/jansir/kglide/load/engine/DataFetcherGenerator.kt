package com.jansir.kglide.load.engine

import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.data.DataFetcher

interface DataFetcherGenerator {
    fun startNext():Boolean
    fun cancel()
    interface FetcherReadyCallback{
        fun reschedule()
        fun onDataFetcherReady(
            sourceKey: Key,
            data: Any,
            fetcher: DataFetcher<*>,
            dataSource: DataSource?,
            attemptedKey: Key
        )
        fun onDataFetcherFailed(
            attemptedKey: Key,
            e: Exception?,
            fetcher: DataFetcher<*>,
            dataSource: DataSource
        )
    }
}