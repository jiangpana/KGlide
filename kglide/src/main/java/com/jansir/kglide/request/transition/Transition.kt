package com.jansir.kglide.request.transition

import android.graphics.drawable.Drawable
import android.view.View

interface Transition<in R>  {
    interface ViewAdapter {
        val view: View
        val currentDrawable: Drawable?
        fun setDrawable(drawable: Drawable?)
    }

    fun transition(current: R, adapter: ViewAdapter?): Boolean

}