package com.jansir.kglide.load.model


interface ModelLoaderFactory<T, Y> {
    fun build(multiFactory:MultiModelLoaderFactory):ModelLoader<T, Y>
}