package com.jansir.kglide.load.resource.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.renderscript.Element
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.engine.Resource
import java.io.InputStream

class BitmapDrawableDecoder<DataType>(val resources: Resources, val decoder: ResourceDecoder<DataType, Bitmap>) :
    ResourceDecoder<DataType, BitmapDrawable> {
    override fun handles(source: DataType, options: Options): Boolean {
       return decoder.handles(source,options)
    }

    override fun decode(source: DataType, width: Int, height: Int, options: Options): Resource<BitmapDrawable>? {
        val bitmapResource =decoder.decode(source, width, height, options)
        return LazyBitmapDrawableResource.obtain(resources,bitmapResource)
    }

}
