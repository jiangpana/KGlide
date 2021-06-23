package com.jansir.kglide

import android.content.Context
import android.content.ContextWrapper
import android.widget.ImageView
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.request.target.ImageViewTargetFactory
import com.jansir.kglide.request.target.ViewTarget

class GlideContext(base: Context,val arrayPool:ArrayPool) : ContextWrapper(base.applicationContext) {

    private val registry =Registry()
    fun getRegistry()=registry
    private val imageViewTargetFactory: ImageViewTargetFactory = ImageViewTargetFactory()

    fun <X>  buildImageViewTarget(
        view: ImageView,
        transcodeClass: Class<X>
    ): ViewTarget<ImageView, X> {
      return  imageViewTargetFactory.buildTarget(view,transcodeClass)
    }

}