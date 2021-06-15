package com.jansir.kglide.load.resource.transcode

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.resource.transcode.ResourceTranscoder
import com.jansir.kglide.load.resource.bitmap.LazyBitmapDrawableResource

class BitmapDrawableTranscoder(val resources: Resources) :ResourceTranscoder<Bitmap,BitmapDrawable> {
    override fun transcode(
        toTranscode: Resource<Bitmap>,
        options: Options
    ): Resource<BitmapDrawable> {
        return LazyBitmapDrawableResource.obtain(resources, toTranscode)!!
    }

}