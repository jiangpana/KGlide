package com.jansir.kglide.load.model

import androidx.core.util.Pools
import java.util.*

class ModelLoaderRegistry(val throwableListPool: Pools.Pool<List<Throwable>>? = null) {
    private val multiModelLoaderFactory: MultiModelLoaderFactory =
        MultiModelLoaderFactory(throwableListPool)

    fun <Model, Data> append(
        modelClass: Class<Model>, dataClass: Class<Data>,
        factory: ModelLoaderFactory<out Model, out Data>
    ) {
        multiModelLoaderFactory.append(modelClass, dataClass, factory)
    }

    fun <A : Any> getModelLoaders(model: A): List<ModelLoader<A, *>> {
        val modelLoaders: List<ModelLoader<A, *>> =
            getModelLoadersForClass(getClass(model))
        return modelLoaders
    }

    private fun <A> getModelLoadersForClass(modelClass: Class<A>): List<ModelLoader<A, *>> {
//        return listOf(multiModelLoaderFactory.build(modelClass))
        return emptyList()
    }

    private fun <A : Any> getClass(model: A): Class<A> {
        return model.javaClass
    }
}