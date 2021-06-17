package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.DataRewinder


class LoadPath<Data, ResourceType, Transcode>(
    dataClass: Class<Data>,
    resourceClass: Class<ResourceType>, transcodeClass: Class<Transcode>,
   val decodePaths: List<DecodePath<Data, ResourceType, Transcode>>,
    listPool: Pools.Pool<List<Throwable>>?=null
) {
    fun load(
        rewinder: DataRewinder<Data>,
        options: Options,
        width: Int,
        height: Int,
        decodeCallback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
       printThis("load")
        return loadWithExceptionList(rewinder, options, width, height, decodeCallback)
    }

    private fun loadWithExceptionList(
        rewinder: DataRewinder<Data>,
        options: Options,
        width: Int,
        height: Int,
        decodeCallback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
        var result: Resource<Transcode>? = null
       decodePaths.forEach {
            try {
                result = it.decode(rewinder, width, height, options, decodeCallback)
            } catch (e: Exception) {

            }
            if (result != null) {
                return@forEach
            }
        }

        return result
    }

}