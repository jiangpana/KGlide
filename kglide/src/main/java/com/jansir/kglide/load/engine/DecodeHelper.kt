package com.jansir.kglide.load.engine

import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.model.ModelLoader

class DecodeHelper<Transcode> {

    private lateinit var diskCacheStrategy: DiskCacheStrategy
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

        this.diskCacheStrategy = diskCacheStrategy
    }

    fun getLoadData(): List<ModelLoader.LoadData<*>> {
        return emptyList()
    }

    fun getDiskCacheStrategy(): DiskCacheStrategy {
        return diskCacheStrategy
    }

    fun hasLoadPath(dataClass: Class<*>): Boolean {
        return true
    }
}