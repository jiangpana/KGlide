package com.jansir.kglide.load.resource.bitmap

import android.graphics.Bitmap
import com.jansir.kglide.load.EncodeStrategy
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceEncoder
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.File

class BitmapEncoder(val arrayPool:ArrayPool): ResourceEncoder<Bitmap> {
    override fun getEncodeStrategy(options: Options): EncodeStrategy {
        //变换之后的
        return EncodeStrategy.TRANSFORMED
    }

    override fun encode(data: Resource<Bitmap>, file: File, options: Options) {
    }
}