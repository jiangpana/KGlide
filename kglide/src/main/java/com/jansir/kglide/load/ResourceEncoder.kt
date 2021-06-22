package com.jansir.kglide.load

import com.jansir.kglide.load.engine.Resource


interface ResourceEncoder<T> :Encoder<Resource<T>>{
    fun getEncodeStrategy(options: Options):EncodeStrategy
}