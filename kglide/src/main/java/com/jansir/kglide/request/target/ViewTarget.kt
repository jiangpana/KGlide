package com.jansir.kglide.request.target

import android.graphics.drawable.Drawable
import android.view.View
import com.jansir.kglide.request.Request
import com.jansir.kglide.request.transition.Transition

abstract class ViewTarget<T : View, Z> (override val view :T): Target<Z> ,Transition.ViewAdapter{
    private var request: Request? = null

    override fun setRequest(req: Request?) {
        request = req
    }

    override fun getRequest(): Request? {
        return request
    }

    override fun onLoadStarted(placeholder: Drawable?) {
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
    }

    override fun onResourceReady(resource: Z, transition: Transition<Z?>?) {

    }

    override fun onLoadCleared(placeholder: Drawable?) {
    }

    override fun getSize(cb: SizeReadyCallback) {
    }

    override fun removeCallback(cb: SizeReadyCallback) {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }




}