package com.jansir.kglide

import com.jansir.kglide.load.model.ModelLoader
import com.jansir.kglide.load.model.ModelLoaderFactory
import com.jansir.kglide.load.model.ModelLoaderRegistry


class Registry {
    private val modelLoaderRegistry =ModelLoaderRegistry()


    fun  <Model, Data> append(
        modelClass:Class<Model> ,
        dataClass:Class<Data>,
        factory: ModelLoaderFactory<Model, Data>
    ){
        modelLoaderRegistry.append(modelClass, dataClass, factory)
    }

    fun <Model : Any> getModelLoaders(model: Model): List<ModelLoader<Model, *>>{
        return modelLoaderRegistry.getModelLoaders(model)
    }
}