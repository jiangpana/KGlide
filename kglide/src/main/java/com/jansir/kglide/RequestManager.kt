package com.jansir.kglide

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.jansir.kglide.ext.isOnMainThread
import com.jansir.kglide.manager.*
import com.jansir.kglide.request.Request
import com.jansir.kglide.request.target.Target


class RequestManager(
    val kGlide: KGlide,
    val lifecycle: Lifecycle,
    val requestManagerTreeNode: RequestManagerTreeNode,
    val context: Context
) : LifecycleListener, ModelTypes<RequestBuilder<Drawable>>{
    private val addSelfToLifecycle = Runnable { lifecycle.addListener(this@RequestManager) }
    private val mainHandler = Handler(Looper.getMainLooper())
    private val targetTracker = TargetTracker()
    private var connectivityMonitor: ConnectivityMonitor? = null
    private val connectivityMonitorFactory: ConnectivityMonitorFactory =kGlide.connectivityMonitorFactory
    private val requestTracker = RequestTracker()
    init {
        if (isOnMainThread()) {
            lifecycle.addListener(this)
        } else {
            mainHandler.post(addSelfToLifecycle)
        }
        connectivityMonitor= connectivityMonitorFactory.build(
            context.applicationContext,
            RequestManagerConnectivityListener(requestTracker)
        )
        lifecycle.addListener(connectivityMonitor!!)
    }

    override fun onStart() {
        resumeRequests()
        targetTracker.onStart()
    }


    override fun onStop() {
        pauseRequests()
        targetTracker.onStop()
    }

    private fun resumeRequests() {
        requestTracker.resumeRequests()
    }


    private fun pauseRequests() {
        requestTracker.pauseRequests()
    }

    @Synchronized
    fun pauseAllRequests() {
        requestTracker.pauseAllRequests()
    }

    override fun onDestroy() {
        for (target in targetTracker.getAll()) {
            clear(target)
        }
        targetTracker.onDestroy()
        targetTracker.clear()
        requestTracker.clearRequests()
        lifecycle.removeListener(this)
        lifecycle.removeListener(connectivityMonitor!!)
        mainHandler.removeCallbacks(addSelfToLifecycle)
        kGlide.unregisterRequestManager(this)
    }


    @Synchronized
    fun track(target: Target<*>, request: Request) {
        targetTracker.track(target)
        requestTracker.runRequest(request)
    }

    @Synchronized
    fun untrack(target:Target<*>): Boolean {
        val request = target.getRequest() ?: return true
        // If the Target doesn't have a request, it's already been cleared.
        return if (requestTracker.clearAndRemove(request)) {
            targetTracker.untrack(target)
            target.setRequest(null)
            true
        } else {
            false
        }
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

    inner class RequestManagerConnectivityListener(val requestTracker:RequestTracker): ConnectivityMonitor.ConnectivityListener {
        override fun onConnectivityChanged(isConnected: Boolean) {
            if (isConnected) {
                synchronized(this@RequestManager) { requestTracker.restartRequests() }
            }
        }

    }
}