package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import com.jansir.kglide.load.EncodeStrategy
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceEncoder
import com.jansir.kglide.load.data.BufferedOutputStream
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class BitmapEncoder(val arrayPool: ArrayPool) : ResourceEncoder<Bitmap> {
    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        //变换之后的
        return EncodeStrategy.TRANSFORMED
    }

    //todo encode还须优化
    override fun encode(resource: Resource<Bitmap>, file: File, options: Options): Boolean {
        val bitmap: Bitmap = resource.get()
        val format: CompressFormat = getFormat(bitmap, options)
        return try {
//            options.get(BitmapEncoder.COMPRESSION_QUALITY)
            val quality: Int = 30
            var success = false
            var os: OutputStream? = null
            try {
                os = FileOutputStream(file)
//                os = BufferedOutputStream(os, arrayPool)
                bitmap.compress(format, quality, os)
                os.close()
                success = true
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                if (os != null) {
                    try {
                        os.close()
                    } catch (e: IOException) {
                        // Do nothing.
                    }
                }
            }
            success
        } finally {

        }
    }

    private fun getFormat(bitmap: Bitmap, options: Options): CompressFormat {
//        val format: CompressFormat = options.get(BitmapEncoder.COMPRESSION_FORMAT)
//        return format
//            ?: if (bitmap.hasAlpha()) {
//                CompressFormat.PNG
//            } else {
//                CompressFormat.JPEG
//            }
        return  CompressFormat.JPEG
    }
}