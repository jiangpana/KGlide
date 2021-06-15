package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.InputStream

class StreamBitmapDecoder(
    downsampler: Downsampler,
    byteArrayPool: ArrayPool
) : ResourceDecoder<InputStream, Bitmap> {
    val downsampler = Downsampler()
    override fun handles(source: InputStream, options: Options): Boolean {
        return downsampler.handles(source)
    }

    override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap>? {
        return null
    }
}