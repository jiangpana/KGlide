package com.jansir.kglide

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.jansir.kglide.ext.isOnMainThread
import com.jansir.kglide.manager.Lifecycle
import com.jansir.kglide.manager.LifecycleListener
import com.jansir.kglide.manager.RequestManagerTreeNode
import com.jansir.kglide.manager.TargetTracker
import com.jansir.kglide.request.target.Target


class RequestManager(
    val kGlide: KGlide,
    val lifecycle: Lifecycle,
    val requestManagerTreeNode: RequestManagerTreeNode,
    val context: Context
) : LifecycleListener, ModelTypes<RequestBuilder<Drawable>> {
    private val addSelfToLifecycle = Runnable { lifecycle.addListener(this@RequestManager) }
    private val mainHandler = Handler(Looper.getMainLooper())
    private val targetTracker = TargetTracker()
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

    fun clear(target:Target<*>){
        untrackOrDelegate(target)
    }

    private fun untrackOrDelegate(target: Target<*>) {

    }
}