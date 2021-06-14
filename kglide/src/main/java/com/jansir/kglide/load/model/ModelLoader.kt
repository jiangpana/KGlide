package com.jansir.kglide.load.model

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.DataFetcher

interface ModelLoader<Model, Data> {

    class LoadData<Data>(val sourceKey: Key, val alternateKeys:List<Key> = emptyList(), val fetcher: DataFetcher<Data>) {

    }

    fun buildLoadData(
        model: Model,
        width: Int,
        height: Int,
        options: Options
    ): LoadData<Data>?

    fun handles(model: Model): Boolean
}