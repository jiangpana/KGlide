package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.InputStream

class StreamBitmapDecoder(
    val downsampler: Downsampler,
    byteArrayPool: ArrayPool
) : ResourceDecoder<InputStream, Bitmap> {

    override fun handles(source: InputStream, options: Options): Boolean {
        return downsampler.handles(source)
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap>? {
        printThis(" decode -> width=$width , height=$height")
        var callbacks: Downsampler.DecodeCallbacks?=null
        return downsampler.decode(source  ,width,height,options,callbacks)
    }
}