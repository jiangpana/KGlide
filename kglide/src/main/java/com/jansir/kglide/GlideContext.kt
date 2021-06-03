package com.jansir.kglide

import android.content.Context
import android.content.ContextWrapper
import android.widget.ImageView
import com.jansir.kglide.request.target.ImageViewTargetFactory
import com.jansir.kglide.request.target.ViewTarget

class GlideContext(base: Context) : ContextWrapper(base.applicationContext) {

    private val imageViewTargetFactory: ImageViewTargetFactory = ImageViewTargetFactory()

    fun <X>  buildImageViewTarget(
        view: ImageView,
        transcodeClass: Class<X>
    ): ViewTarget<ImageView, X> {
      return  imageViewTargetFactory.buildTarget(view,transcodeClass)
    }
}