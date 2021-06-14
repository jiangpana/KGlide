package com.jansir.kglide.load.engine.resource.transcode

import com.jansir.kglide.load.Options
import com.jansir.kglide.load.engine.Resource


interface ResourceTranscoder<Z, R>  {
    fun transcode(toTranscode: Resource<Z>,options : Options):Resource<R>
}