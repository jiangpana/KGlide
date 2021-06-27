package com.jansir.kglide

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import com.jansir.kglide.ext.isOnMainThread
import com.jansir.kglide.manager.*
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

    private fun resumeRequests() {

    }

    override fun onStop() {
        pauseRequests()
        targetTracker.onStop()
    }

    private fun pauseRequests() {

    }

    override fun onDestroy() {
//        for (target in targetTracker.getAll()) {
//            clear(target!!)
//        }
        targetTracker.onDestroy()
        targetTracker.clear()
        requestTracker.clearRequests()
        lifecycle.removeListener(this)
        lifecycle.removeListener(connectivityMonitor!!)
        mainHandler.removeCallbacks(addSelfToLifecycle)
        kGlide.unregisterRequestManager(this)
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