package com.jansir.kglide.load.model

import androidx.core.util.Pools

class ModelLoaderRegistry(val throwableListPool: Pools.Pool<List<Throwable>>? = null) {

    private val multiModelLoaderFactory: MultiModelLoaderFactory =
        MultiModelLoaderFactory(throwableListPool)

    private val cache = ModelLoaderCache()

    fun <Model, Data> append(
        modelClass: Class<Model>, dataClass: Class<Data>,
        factory: ModelLoaderFactory<out Model, out Data>
    ) {
        multiModelLoaderFactory.append(modelClass, dataClass, factory)
    }

    fun <A : Any> getModelLoaders(model: A): List<ModelLoader<A, *>> {
        val modelLoaders: List<ModelLoader<A, *>> =
            getModelLoadersForClass(getClass(model))
        check(modelLoaders.isNotEmpty()) { "ModelLoaders Must Not Empty!" }
        val filteredLoaders = mutableListOf<ModelLoader<A, *>>()
        modelLoaders.forEach {
            if (it.handles(model)) {
                filteredLoaders.add(it)
            }
        }
        check(filteredLoaders.isNotEmpty()) { "FilteredLoaders Must Not Empty!" }
        return filteredLoaders
    }

    private fun <A> getModelLoadersForClass(modelClass: Class<A>): List<ModelLoader<A, *>> {
        cache.get(modelClass)?.let {
            return it
        }
        val loaders = multiModelLoaderFactory.build(modelClass)
        cache.put(modelClass, loaders)
        return loaders
    }

    private fun <A : Any> getClass(model: A): Class<A> {
        return model.javaClass
    }

    class ModelLoaderCache() {
        private val cachedModelLoaders = hashMapOf<Class<*>, Entry<*>>()
        fun clear() {
            cachedModelLoaders.clear()
        }

        fun <Model> put(
            modelClass: Class<Model>,
            loaders: List<ModelLoader<Model, *>>
        ) {
            val previous: Entry<*>? =
                cachedModelLoaders.put(
                    modelClass,
                    Entry(
                        loaders
                    )
                )
            check(previous == null) { "Already cached loaders for model: $modelClass" }
        }

        operator fun <Model> get(modelClass: Class<Model>): List<ModelLoader<Model, *>>? {
            val entry: Entry<Model>? =
                cachedModelLoaders[modelClass] as Entry<Model>?
            return entry?.loaders
        }
    }

    class Entry<Model>(val loaders: List<ModelLoader<Model, *>>)
}