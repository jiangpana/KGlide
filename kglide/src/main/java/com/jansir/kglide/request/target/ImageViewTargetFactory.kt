package com.jansir.kglide.request.target

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class ImageViewTargetFactory {
    fun <Z> buildTarget(
        view: ImageView, clazz: Class<Z>
    ): ViewTarget<ImageView, Z> {
        return if (Bitmap::class.java == clazz) {
            BitmapImageViewTarget(view) as ViewTarget<ImageView, Z>
        } else if (Drawable::class.java.isAssignableFrom(clazz)) {
            DrawableImageViewTarget(view) as ViewTarget<ImageView, Z>
        } else {
            throw IllegalArgumentException(
                "Unhandled class: $clazz, try .as*(Class).transcode(ResourceTranscoder)"
            )
        }
    }
}