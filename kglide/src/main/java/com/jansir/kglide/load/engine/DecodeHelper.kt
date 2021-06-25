package com.jansir.kglide.load.engine

import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.*
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.engine.cache.DiskCache
import com.jansir.kglide.load.model.ModelLoader
import java.io.File
import java.util.*

class DecodeHelper<Transcode> {

    private lateinit var diskCacheProvider: DecodeJob.DiskCacheProvider
    lateinit var signature: Key
    private lateinit var transformations: Map<Class<*>, Transformation<*>>
    private lateinit var resourceClass: Class<*>
    private lateinit var transcodeClass: Class<Transcode>
    private lateinit var priority: Priority
    lateinit var options: Options
    var width: Int = 0
    var height: Int = 0
    private lateinit var model: Any
    private lateinit var glideContext: GlideContext
    private lateinit var diskCacheStrategy: DiskCacheStrategy
    private val loadData = ArrayList<ModelLoader.LoadData<*>>()
    private var isLoadDataSet = false
    private var isCacheKeysSet = false
    fun init(
        glideContext: GlideContext,
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        diskCacheStrategy: DiskCacheStrategy,
        resourceClass: Class<*>,
        transcodeClass: Class<Transcode>,
        priority: Priority,
        options: Options,
        transformations: Map<Class<*>, Transformation<*>>,
        transformationRequired: Boolean,
        scaleOnlyOrNoTransform: Boolean,
        diskCacheProvider: DecodeJob.DiskCacheProvider
    ) {
        this.priority = priority;
        this.model = model;
        this.width = width;
        this.height = height;
        this.options = options;
        this.glideContext = glideContext
        this.diskCacheStrategy = diskCacheStrategy
        this.resourceClass = resourceClass
        this.transcodeClass = transcodeClass
        this.transformations = transformations
        this.signature =signature
        this.diskCacheProvider=diskCacheProvider
    }


    fun getLoadData(): List<ModelLoader.LoadData<*>> {
        if (!isLoadDataSet) {
            isLoadDataSet = true
            loadData.clear()
            val modelLoaders = glideContext.getRegistry().getModelLoaders(model)
            modelLoaders.forEach {
                val current = it.buildLoadData(model, width, height, options);
                current?.let {
                    loadData.add(current)
                }
            }
        }
        return loadData
    }

    fun getDiskCacheStrategy(): DiskCacheStrategy {
        return diskCacheStrategy
    }

    fun hasLoadPath(dataClass: Class<*>): Boolean {
        return true
    }

    fun getPriority(): Priority {
        return priority
    }

    //dataClass = inputstream
    //transcodeClass = drawable ,
    fun <Data> getLoadPath(dataClass: Class<Data>): LoadPath<Data, *, Transcode> {
        return glideContext.getRegistry().getLoadPath(dataClass, resourceClass, transcodeClass)!!
    }

    fun <Z> getTransformation(resourceSubClass: Class<Z>): Transformation<Z>? {
        try {
            return transformations[resourceClass] as Transformation<Z>?
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun isResourceEncoderAvailable(resource: Resource<*>?): Boolean {
        return glideContext.getRegistry().isResourceEncoderAvailable(resource!!)

    }

    fun <Z> getResultEncoder(resource: Resource<Z>?): ResourceEncoder<Z>? {
        return glideContext.getRegistry().getResultEncoder(resource)
    }

    fun isSourceKey(key: Key): Boolean {
        getLoadData().forEach {
            if (it.sourceKey == key) {
                printThis("isSourceKey = true")
                return true
            }
        }
        return false
    }

    fun getArrayPool(): ArrayPool {
        return glideContext.arrayPool
    }

    fun <X> getSourceEncoder(data: X): Encoder<X> {
        return glideContext.getRegistry().getSourceEncoder(data)
    }

    fun getDiskCache(): DiskCache {
        return diskCacheProvider.diskCache
    }


    fun getModelLoaders(file: File): List<ModelLoader<File, *>> {
        return glideContext.getRegistry().getModelLoaders(file)
    }
    private val cacheKeys = ArrayList<Key>()

    fun getCacheKeys(): List<Key> {
        if (!isCacheKeysSet) {
            isCacheKeysSet = true
            cacheKeys.clear()
            val loadData = getLoadData();
            loadData.forEach {
                if (!cacheKeys.contains(it.sourceKey)) {
                    cacheKeys.add(it.sourceKey)
                }
                it.alternateKeys.forEach {alternateKey->
                    if (!cacheKeys.contains(alternateKey)){
                        cacheKeys.add(alternateKey)
                    }
                }
            }
        }

        return cacheKeys
    }


    fun getRegisteredResourceClasses(): List<Class<*>> {
        return glideContext
            .getRegistry()
            .getRegisteredResourceClasses(model.javaClass, resourceClass, transcodeClass)
    }
}