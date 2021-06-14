package com.jansir.kglide.load.engine

import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.data.DataFetcher
import com.jansir.kglide.load.model.ModelLoader
import java.io.InputStream

class SourceGenerator(
    val helper: DecodeHelper<*>,
    val cb: DataFetcherGenerator.FetcherReadyCallback
) : DataFetcherGenerator, DataFetcherGenerator.FetcherReadyCallback {

    companion object {
        private const val TAG = "SourceGenerator"
    }

    @Volatile
    private var loadData: ModelLoader.LoadData<*>? = null
    private val sourceCacheGenerator: DataCacheGenerator? = null
    private val dataToCache: Any? = null
    private var loadDataListIndex = 0

    override fun startNext(): Boolean {

        loadData = null
        var started = false
        while (!started && hasNextModelLoader()) {
            loadData = helper.getLoadData()[loadDataListIndex++]
            loadData?.let {
                if (helper.getDiskCacheStrategy().isDataCacheable(it.fetcher.getDataSource())
                    || helper.hasLoadPath(it.fetcher.getDataClass())
                ) {
                    started = true
                    startNextLoad(it)
                }
            }
        }
        return started
    }

    private fun startNextLoad(loadData: ModelLoader.LoadData<*>) {
        println("SourceGenerator -> startNextLoad")
        loadData.fetcher.loadData(helper.getPriority(), object : DataFetcher.DataCallback<Any?> {
            override fun onDataReady(data: Any?) {
                println("startNextLoad -> onDataReady")
                println("data = ${(data as InputStream).readBytes().size}")
                cb.onDataFetcherReady(
                    loadData.sourceKey,
                    data,
                    loadData.fetcher,
                    loadData.fetcher.getDataSource(),
                    loadData.sourceKey
                )
            }

            override fun onLoadFailed(e: Exception) {
            }
        })
    }

    private fun hasNextModelLoader(): Boolean {
        return loadDataListIndex < helper.getLoadData().size
    }

    override fun cancel() {
    }

    override fun reschedule() {
    }

    override fun onDataFetcherReady(
        sourceKey: Key,
        data: Any,
        fetcher: DataFetcher<*>,
        dataSource: DataSource?,
        attemptedKey: Key
    ) {
    }

    override fun onDataFetcherFailed(
        attemptedKey: Key,
        e: Exception?,
        fetcher: DataFetcher<*>,
        dataSource: DataSource
    ) {
    }


}