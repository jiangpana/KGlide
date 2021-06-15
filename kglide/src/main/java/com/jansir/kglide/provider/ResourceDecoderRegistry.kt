package com.jansir.kglide.provider

import com.jansir.kglide.R
import com.jansir.kglide.load.ResourceDecoder

class ResourceDecoderRegistry {

    private val bucketPriorityList = arrayListOf<String>()
    private val decoders = hashMapOf<String, List<Entry<*, *>>>()

    fun <T, R> append(
        bucket: String,
        decoder: ResourceDecoder<T, R>,
        dataClass: Class<T>,
        resourceClass: Class<R>
    ) {
        getOrAddEntryList(bucket).add(Entry(dataClass, resourceClass, decoder))
    }

    private fun getOrAddEntryList(bucket: String): MutableList<Entry<*, *>> {
        if (!bucketPriorityList.contains(bucket)) {
            bucketPriorityList.add(bucket)
        }
      var entries=  decoders.get(bucket)
        if (entries==null){
            entries= ArrayList()
            decoders.put(bucket,entries)
        }
        return entries as MutableList<Entry<*, *>>
    }

    fun <T, R> getResourceClasses(dataClass: Class<T>, resourceClass: Class<R>): List<Class<R>> {
        val result = arrayListOf<Class<R>>()
        bucketPriorityList.forEach {
          val entries= decoders.get(it);
          entries?.forEach {
              if (it.handles(dataClass,resourceClass)&& !result.contains(it.resourceClass)){
                  result.add((it.resourceClass as Class<R>))
              }
          }
        }
        return result
    }

    fun <Data, Transcode> getDecoders(
        dataClass: Class<Data>,
        resourceClass: Class<Transcode>
    ): List<ResourceDecoder<Data, Transcode>> {
        val result = arrayListOf<ResourceDecoder<Data, Transcode>>()
        bucketPriorityList.forEach {
            decoders.get(it)?.let {
                it.forEach {
                    if (it.handles(dataClass, resourceClass)) {
                        result.add(it.decoder as ResourceDecoder<Data,Transcode>)
                    }
                }
            }
        }
        return result
    }

    class Entry<T, R>(
        val dataClass: Class<T>,
        val resourceClass: Class<R>,
        val decoder: ResourceDecoder<T, R>
    ) {

        //dataClass 是子类 ,resourceClass 是父类
        fun handles(
            dataClass: Class<*>,
            resourceClass: Class<*>
        ): Boolean {
            return this.dataClass.isAssignableFrom(dataClass) &&
                    resourceClass.isAssignableFrom(this.resourceClass)
        }
    }
}