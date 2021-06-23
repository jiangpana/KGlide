package com.jansir.kglide.load.model

import com.jansir.kglide.load.Encoder
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.File
import java.io.InputStream

class StreamEncoder (val byteArrayPool:ArrayPool): Encoder<InputStream> {

    //todo encode File
    override fun encode(data: InputStream, file: File, options: Options) {
    }
}