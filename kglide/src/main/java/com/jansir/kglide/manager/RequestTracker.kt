package com.jansir.kglide.manager

import com.jansir.kglide.request.Request
import java.util.*

class RequestTracker {
    companion object{
        private const val TAG = "RequestTracker"

    }

    //弱引用request防止内存泄漏
    private val requests: Set<Request> =
        Collections.newSetFromMap(WeakHashMap<Request, Boolean>())
    private val pendingRequests: List<Request> = ArrayList()
    private val isPaused = false
    fun isPaused(): Boolean {
        return isPaused
    }

    fun restartRequests() {

    }

    fun clearRequests(){

    }

}