package com.jansir.kglide.load.engine

import androidx.core.util.Pools
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.engine.resource.transcode.ResourceTranscoder


class DecodePath <DataType, ResourceType, Transcode> (
    dataClass: Class<DataType>,
    resourceClass:Class<ResourceType>,
    transcodeClass: Class<Transcode>,
    decodes:List<out ResourceDecoder<DataType, ResourceType>>,
    transcoders: ResourceTranscoder<ResourceType, Transcode>,
    listPool : Pools.Pool<List<Throwable>>
){

}