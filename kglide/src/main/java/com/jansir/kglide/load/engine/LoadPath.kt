package com.jansir.kglide.load.engine

import androidx.core.util.Pools


class LoadPath<Data, ResourceType, Transcode>(
    dataClass: Class<Data>,
    resourceClass: Class<ResourceType>, transcodeClass: Class<Transcode>,
    decodePath: List<DecodePath<Data, ResourceType, Transcode>>,
    listPool: Pools.Pool<List<Throwable>>
) {

}