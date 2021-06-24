package com.jansir.kglide.load.engine

import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.data.DataFetcher
import com.jansir.kglide.load.model.ModelLoader

class SourceGenerator(
    val helper: DecodeHelper<*>,
    val cb: DataFetcherGenerator.FetcherReadyCallback
) : DataFetcherGenerator, DataFetcherGenerator.FetcherReadyCallback {

    companion object {
        private const val TAG = "SourceGenerator"
    }

    @Volatile
    private var loadData: ModelLoader.LoadData<*>? = null
    private var sourceCacheGenerator: DataCacheGenerator? = null
    private var dataToCache: Any? = null
    private var loadDataListIndex = 0

    override fun startNext(): Boolean {
        printThis("startNext() " +Thread.currentThread().name)
        if (dataToCache!=null){
            val data: Any = dataToCache!!
            dataToCache = null
            cacheData(data)
        }
        if (sourceCacheGenerator != null && sourceCacheGenerator!!.startNext()) {
            return true
        }
        sourceCacheGenerator = null

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

    private fun cacheData(data: Any) {
        try {
            printThis("startNext ->" +Thread.currentThread().name +" -> cacheData()")
            val encoder = helper.getSourceEncoder(data);
            val writer =DataCacheWriter(encoder, data, helper.options);
            printThis("loadData.sourceKey =${loadData!!.sourceKey.javaClass.simpleName} ")
            //loadData!!.sourceKey = KGlideUrl
            val originalKey = DataCacheKey(loadData!!.sourceKey, helper.signature)
            helper.getDiskCache().put(originalKey, writer)
            printThis("${encoder.javaClass.simpleName}")
        } finally {
            loadData?.fetcher?.cleanup()
        }
        sourceCacheGenerator = DataCacheGenerator(cacheKeys =listOf(loadData!!.sourceKey), helper = helper, cb =this)
    }

    private fun startNextLoad(loadData: ModelLoader.LoadData<*>) {
        loadData.fetcher.loadData(helper.getPriority(), object : DataFetcher.DataCallback<Any?> {
            override fun onDataReady(data: Any?) {
                if (isCurrentRequest(loadData)) {
                    onDataReadyInternal(loadData,data!!)
                }
            }

            override fun onLoadFailed(e: Exception) {
                onLoadFailedInternal(loadData,e)
            }
        })
    }

    private fun onDataReadyInternal(loadData: ModelLoader.LoadData<*>, data: Any) {
        val diskCacheStrategy  = helper.getDiskCacheStrategy();
        if (diskCacheStrategy.isDataCacheable(loadData.fetcher.getDataSource())){
            dataToCache = data
            // We might be being called back on someone else's thread. Before doing anything, we should
            // reschedule to get back onto Glide's thread.
            cb.reschedule()
        }else{
            //不支持缓存,直接解码
            cb.onDataFetcherReady(
                loadData.sourceKey,
                data,
                loadData.fetcher,
                loadData.fetcher.getDataSource(),
                loadData.sourceKey
            )
        }

    }

    fun isCurrentRequest(requestLoadData: ModelLoader.LoadData<*>): Boolean {
        val currentLoadData: ModelLoader.LoadData<*>? = loadData
        return currentLoadData != null && currentLoadData === requestLoadData
    }
    private fun onLoadFailedInternal(
        loadData: ModelLoader.LoadData<*>,
        e: Exception
    ) {
        cb.onDataFetcherFailed(loadData.sourceKey,e,loadData.fetcher,loadData.fetcher.getDataSource())
    }

    private fun hasNextModelLoader(): Boolean {
        return loadDataListIndex < helper.getLoadData().size
    }

    override fun cancel() {
        loadData?.fetcher?.cancel()
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
        // This data fetcher will be loading from a File and provide the wrong data source, so override
        // with the data source of the original fetcher
        cb.onDataFetcherReady(
            sourceKey,
            data,
            fetcher,
            loadData!!.fetcher.getDataSource(),
            sourceKey
        )
    }

    override fun onDataFetcherFailed(
        attemptedKey: Key,
        e: Exception?,
        fetcher: DataFetcher<*>,
        dataSource: DataSource
    ) {
    }


}