package com.jansir.kglide.load.engine

import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.model.ModelLoader
import java.util.*

class DecodeHelper<Transcode> {

    private lateinit var priority: Priority
    private lateinit var options: Options
    private var width: Int = 0
    private var height: Int = 0
    private lateinit var model: Any
    private lateinit var glideContext: GlideContext
    private lateinit var diskCacheStrategy: DiskCacheStrategy
    private val loadData = ArrayList<ModelLoader.LoadData<*>>()
    private var isLoadDataSet = false

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
}