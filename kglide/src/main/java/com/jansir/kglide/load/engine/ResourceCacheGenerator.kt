package com.jansir.kglide.load.engine

import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.data.DataFetcher
import com.jansir.kglide.load.model.ModelLoader
import java.io.File

class ResourceCacheGenerator(
    val helper: DecodeHelper<*>,
    val cb: DataFetcherGenerator.FetcherReadyCallback
) : DataFetcherGenerator, DataFetcher.DataCallback<Any?> {

    private var modelLoaders: List<ModelLoader<File, *>>? = null
    private var modelLoaderIndex = 0
    private var resourceClassIndex = -1
    private var sourceIdIndex = 0
    private var currentKey: ResourceCacheKey? = null
    private var cacheFile: File? = null
    private var sourceKey: Key? = null

    @Volatile
    private var loadData: ModelLoader.LoadData<*>? = null


    override fun startNext(): Boolean {
        printThis("startNext() " + Thread.currentThread().name)
        val sourceIds = helper.getCacheKeys();
        if (sourceIds.isEmpty()) return false
        val resourceClasses = helper.getRegisteredResourceClasses();
        if (resourceClasses.isEmpty()) {
            return false
        }
        //首先找到cacheFile 再通过cacheFile获取modelLoaders,如果没有cacheFile则返回false
        while (modelLoaders == null || !hasNextModelLoader()) {
            resourceClassIndex++
            if (resourceClassIndex >= resourceClasses.size) {
                sourceIdIndex++
                // 如果通过sourceId和resourceClass都没有拿到cacheFile,则返回false
                if (sourceIdIndex >= sourceIds.size) {
                    printThis("sourceIdIndex >= sourceIds.size  return false"  )
                    return false
                }
                resourceClassIndex = 0
            }
            val sourceId = sourceIds[sourceIdIndex];
            val resourceClass = resourceClasses[resourceClassIndex]
            val transformation = helper.getTransformation(resourceClass)
            currentKey = ResourceCacheKey( // NOPMD AvoidInstantiatingObjectsInLoops
                helper.getArrayPool(),
                sourceId,
                helper.signature,
                helper.width,
                helper.height,
                transformation,
                resourceClass,
                helper.options
            )
            cacheFile = helper.getDiskCache()[currentKey!!]
            if (cacheFile != null) {
                sourceKey = sourceId
                modelLoaders = helper.getModelLoaders(cacheFile!!)
                modelLoaderIndex = 0
            }
        }

        loadData = null
        var started = false
        while (!started && hasNextModelLoader()) {
          val  modelLoader = modelLoaders!!.get(modelLoaderIndex++);
            loadData = modelLoader.buildLoadData(
                cacheFile!!, helper.width, helper.height, helper.options)
            if (loadData != null && helper.hasLoadPath(loadData!!.fetcher.getDataClass())) {
                started = true
                loadData!!.fetcher.loadData(helper.getPriority(), this)
            }
        }
        return started
    }

    private fun hasNextModelLoader(): Boolean {
        return modelLoaderIndex < modelLoaders!!.size
    }

    override fun cancel() {
    }

    override fun onDataReady(data: Any?) {
        printThis("onDataReady")
        cb.onDataFetcherReady(
            sourceKey!!, data!!, loadData!!.fetcher, DataSource.RESOURCE_DISK_CACHE, currentKey!!
        )
    }

    override fun onLoadFailed(e: Exception) {
        e.printStackTrace()
        printThis("onLoadFailed")
    }
}