package com.jansir.kglide.request.target

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.jansir.kglide.request.transition.Transition

abstract class ImageViewTarget<Z>(override val view: ImageView) : ViewTarget<ImageView, Z>(view) {

    private fun setResourceInternal(resource: Z?) {
        // Order matters here. Set the resource first to make sure that the Drawable has a valid and
        // non-null Callback before starting it.
        setResource(resource)
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
        super.onLoadFailed(errorDrawable)
        setResourceInternal(null)
        setDrawable(errorDrawable)
    }

    override fun onResourceReady(resource: Z, transition: Transition<Z?>?) {
        super.onResourceReady(resource, transition)
        setResourceInternal(resource)
    }

    override fun onLoadCleared(placeholder: Drawable?) {
        super.onLoadCleared(placeholder)
        setResourceInternal(null)
        setDrawable(placeholder)
    }

    override fun setDrawable(drawable: Drawable?) {
        view.setImageDrawable(drawable)
    }

    protected abstract fun setResource(resource: Z?)
}