package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.data.DataRewinder
import com.jansir.kglide.load.engine.resource.transcode.ResourceTranscoder


class DecodePath <DataType, ResourceType, Transcode> (
    dataClass: Class<DataType>,
    resourceClass:Class<ResourceType>,
    transcodeClass: Class<Transcode>,
   val decoders:List<out ResourceDecoder<DataType, ResourceType>>,
   val transcoder: ResourceTranscoder<ResourceType, Transcode>,
    listPool : Pools.Pool<List<Throwable>>?=null
){


    fun decode(
        rewinder: DataRewinder<DataType>,
        width: Int,
        height: Int,
        options: Options,
        callback: DecodePath.DecodeCallback<ResourceType>
    ): Resource<Transcode>? {
        val decoded: Resource<ResourceType>? = decodeResource(rewinder, width, height, options)
        val transformed: Resource<ResourceType> = callback.onResourceDecoded(decoded!!)
        return transcoder.transcode(transformed, options)
    }


    private fun decodeResource(
        rewinder: DataRewinder<DataType>, width: Int, height: Int, options: Options
    ): Resource<ResourceType>? {
        return try {
            decodeResourceWithList(rewinder, width, height, options)
        } finally {

        }
    }

    private fun decodeResourceWithList(
        rewinder: DataRewinder<DataType>,
        width: Int,
        height: Int,
        options: Options
    ): Resource<ResourceType>? {
        var result: Resource<ResourceType>? = null
        decoders.forEach {
            var data = rewinder.rewindAndGet()
            if (it.handles(data, options)) {
                data = rewinder.rewindAndGet()
                result = it.decode(data, width, height, options)
            }
            if (result!=null)return result
        }
        return result
    }


    interface DecodeCallback<ResourceType> {
        fun onResourceDecoded(resource: Resource<ResourceType>): Resource<ResourceType>
    }
}