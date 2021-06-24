package com.jansir.kglide.load.engine

import com.jansir.kglide.load.Encoder
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.engine.cache.DiskCache
import java.io.File

class DataCacheWriter<DataType>(val encoder:Encoder<DataType> , val data:DataType ,val options :Options): DiskCache.Writer {
    override fun write(file: File): Boolean {
        return encoder.encode(data, file, options)
    }
}