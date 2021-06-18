package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.InputStreamRewinder
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.engine.bitmap_recycle.BitmapPool
import com.jansir.kglide.load.engine.resource.bitmap.BitmapResource
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class Downsampler(
    val bitmapPool: BitmapPool, val byteArrayPool: ArrayPool
) {
    fun handles(source: InputStream): Boolean {
        return true
    }

    fun decode(
        ris: InputStream,
        width: Int,
        height: Int,
        options: Options,
        callbacks: DecodeCallbacks?
    ): Resource<Bitmap>? {

        val options = BitmapFactory.Options()
        options.inScaled = false
        options.inMutable = true
        var bitmap: Bitmap
        ris.reset()
        bitmap = BitmapFactory.decodeStream(ris)!!
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    interface DecodeCallbacks {
        fun onObtainBounds()

        @Throws(IOException::class)
        fun onDecodeComplete(bitmapPool: BitmapPool?, downsampled: Bitmap?)
    }
}