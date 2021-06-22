package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.engine.bitmap_recycle.BitmapPool
import com.jansir.kglide.load.engine.resource.bitmap.BitmapResource
import com.jansir.kglide.util.Util
import java.io.IOException
import java.io.InputStream

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
        var bitmap: Bitmap
        val options = BitmapFactory.Options()
        ris.reset()
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ris,null,options)
        options.inJustDecodeBounds = false;
        val sourceHeight =options.outHeight
        val sourceWidth =options.outWidth
        printThis("sourceHeight =$sourceHeight sourceWidth =$sourceWidth")
//        options.inSampleSize =8
        options.inTargetDensity=width
        options.inDensity=sourceWidth
        options.inScaled=true
        //把流回到起点
        ris.reset()
        bitmap = BitmapFactory.decodeStream(ris,null,options)!!
        printThis("bitmap size = ${Util.getBitmapByteSize(bitmap)}")
        return BitmapResource.obtain(bitmap, bitmapPool);
    }

    interface DecodeCallbacks {
        fun onObtainBounds()

        @Throws(IOException::class)
        fun onDecodeComplete(bitmapPool: BitmapPool?, downsampled: Bitmap?)
    }
}