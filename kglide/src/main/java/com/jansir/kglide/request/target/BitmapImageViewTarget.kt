package com.jansir.kglide.request.target

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class BitmapImageViewTarget(
    override val view: ImageView,
    override val currentDrawable: Drawable = view.drawable
) : ImageViewTarget<Bitmap>(view) {
    override fun setResource(resource: Bitmap?) {
        view.setImageBitmap(resource)
    }
}