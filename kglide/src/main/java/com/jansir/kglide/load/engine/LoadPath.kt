package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.DataRewinder


class LoadPath<Data, ResourceType, Transcode>(
    dataClass: Class<Data>,
    resourceClass: Class<ResourceType>, transcodeClass: Class<Transcode>,
    decodePath: List<DecodePath<Data, ResourceType, Transcode>>,
    listPool: Pools.Pool<List<Throwable>>?=null
) {
    fun load(
        rewinder: DataRewinder<Data>,
        options: Options?,
        width: Int,
        height: Int,
        decodeCallback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
        return null
    }

}