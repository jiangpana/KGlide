package com.jansir.kglide.request

import android.content.Context
import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.engine.Engine
import com.jansir.kglide.request.target.SizeReadyCallback
import com.jansir.kglide.request.target.Target
import com.jansir.kglide.request.transition.TransitionFactory
import java.util.concurrent.Executor

private class SingleRequest<R>(
    val context: Context,
    glideContext: GlideContext,
    model: Any, transcode: Class<R>, requestOptions: BaseRequestOptions<*>,
    overrideWidth:Int, overrideHeight:Int,priority: Priority,
    target: Target<R>,
    targetListener:RequestListener<R>?=null,
    requestListeners:List<RequestListener<R>>?=null,
            requestCoordinator: RequestCoordinator,
    engine: Engine,
    animationFactory: TransitionFactory<in R>,
    callbackExecutor:Executor
    ) : Request, SizeReadyCallback {
    private val requestLock: Any = Any()
    private var status: Status = Status.PENDING

    companion object{
       /* fun <R> obtain():SingleRequest<R>{

        }*/
    }
    sealed class Status {
        object PENDING : Status()
        object RUNNING : Status()
        object WAITING_FOR_SIZE : Status()
        object COMPLETE : Status()
        object FAILED : Status()
        object CLEARED : Status()
    }

    override fun begin() {
    }

    override fun clear() {
    }

    override fun pause() {
        synchronized(requestLock) {
            if (isRunning()) {
                clear()
            }
        }
    }

    override fun isRunning(): Boolean {
        synchronized(requestLock) {
            return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE
        }
    }

    override fun isComplete(): Boolean {
        synchronized(requestLock) { return status == Status.COMPLETE }
    }

    override fun isAnyResourceSet(): Boolean {
        synchronized(requestLock) { return status == Status.COMPLETE }
    }

    override fun isCleared(): Boolean {
        synchronized(requestLock) { return status == Status.CLEARED }
    }

    override fun isEquivalentTo(request: Request): Boolean {
        return false
    }

    override fun onSizeReady(width: Int, height: Int) {
    }
}