package com.jansir.kglide

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.jansir.kglide.ext.isOnMainThread
import com.jansir.kglide.manager.Lifecycle
import com.jansir.kglide.manager.LifecycleListener
import com.jansir.kglide.manager.RequestManagerTreeNode


class RequestManager(
    val kGlide: KGlide,
    val lifecycle: Lifecycle,
    val requestManagerTreeNode: RequestManagerTreeNode,
    val context: Context
) : LifecycleListener, ModelTypes<RequestBuilder<Drawable>> {
    private val addSelfToLifecycle = Runnable { lifecycle.addListener(this@RequestManager) }
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        if (isOnMainThread()) {
            lifecycle.addListener(this)
        } else {
            mainHandler.post(addSelfToLifecycle)
        }

    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    override fun load(string: String): RequestBuilder<Drawable> {
        return asDrawable().load(string)
    }

    private fun asDrawable(): RequestBuilder<Drawable> {
        return RequestBuilder(
            kGlide,
            requestManager = this,
            transcodeClass = Drawable::class.java,
            context = context
        )
    }
}