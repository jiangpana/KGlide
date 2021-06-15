package com.jansir.kglide.load

import com.jansir.kglide.load.engine.Resource


interface ResourceDecoder<T,Z>{
    fun handles(source :T , options :Options):Boolean
    fun decode(source: T , width: Int , height :Int  , options :Options):Resource<Z>?
}