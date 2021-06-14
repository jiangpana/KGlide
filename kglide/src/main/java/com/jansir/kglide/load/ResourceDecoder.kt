package com.jansir.kglide.load


interface ResourceDecoder<T,Z>{
    fun handles(source :T , options :Options):Boolean
    fun decode(source: T , width: Int , height :Int  , options :Options)
}