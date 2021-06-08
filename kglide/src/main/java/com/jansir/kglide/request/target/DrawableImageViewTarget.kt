package com.jansir.kglide.request.target

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView

class DrawableImageViewTarget(
    override val view: ImageView, override val currentDrawable: Drawable? = view.drawable
) : ImageViewTarget<Drawable>(view) {
    override fun setResource(resource: Drawable?) {
        view.setImageDrawable(resource)
    }
}