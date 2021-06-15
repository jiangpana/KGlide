package com.jansir.kglide.load.resource.transcode

import com.jansir.kglide.load.engine.resource.transcode.ResourceTranscoder

class TranscoderRegistry {

   private val  transcoders = arrayListOf<Entry<*,*>>()

    fun <Z, R> get(resourceClass :Class<Z>,transcodedClass:Class<R>):ResourceTranscoder<Z, R>{
        for (entry in transcoders) {
            if (entry.handles(resourceClass, transcodedClass)) {
                return (entry.transcoder as ResourceTranscoder<Z, R>)
            }
        }
        throw Exception("No transcoder registered to transcode from \" +$resourceClass + \" to \" + $transcodedClass")
    }
    @Synchronized
    fun <Z, R> register(
        decodedClass: Class<Z>,
        transcodedClass: Class<R>,
        transcoder: ResourceTranscoder<Z, R>
    ) {
        transcoders.add(
            Entry(
                decodedClass,
                transcodedClass,
                transcoder
            )
        )
    }
    fun  <Z, R> getTranscodeClasses(
        resourceClass: Class<Z>,
        transcodeClass: Class<R>
    ): List<Class<R>> {
        val transcodeClasses = arrayListOf<Class<R>>()
        transcoders.forEach {
            if (it.handles(resourceClass, transcodeClass)){
                transcodeClasses.add(transcodeClass)
            }
        }
        return transcodeClasses
    }

    class Entry<Z,R>(val fromClass :Class<Z>,
                     val toClass:Class<R>,
                     val transcoder:ResourceTranscoder<Z,R>
    ){
        fun handles(fromClass:Class<*> ,toClass:Class<*>):Boolean{
            return this.fromClass.isAssignableFrom(fromClass) && toClass.isAssignableFrom(this.toClass)
        }
    }
}