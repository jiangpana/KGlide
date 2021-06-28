package com.jansir.kglide.manager

import com.jansir.kglide.request.Request
import com.jansir.kglide.util.Util
import java.util.*

class RequestTracker {
    companion object{
        private const val TAG = "RequestTracker"

    }

    //弱引用request防止内存泄漏
    private val requests =
        Collections.newSetFromMap(WeakHashMap<Request, Boolean>())
    private val pendingRequests = ArrayList<Request>()

    private var isPaused = false

    fun isPaused(): Boolean {
        return isPaused
    }

    fun runRequest(request: Request) {
        requests.add(request)
        if (!isPaused) {
            request.begin()
        } else {
            request.clear()
            pendingRequests.add(request)
        }
    }
    fun addRequest(request: Request?) {
        requests.add(request)
    }

    fun restartRequests() {
        for (request in Util.getSnapshot(requests)) {
            if (!request.isComplete() && !request.isCleared()) {
                request.clear()
                if (!isPaused) {
                    request.begin()
                } else {
                    pendingRequests.add(request)
                }
            }
        }
    }

    fun clearRequests(){
        for (request in Util.getSnapshot(requests)) {
            clearAndRemove(request!!)
        }
        pendingRequests.clear()
    }

    fun pauseRequests() {
        isPaused = true
        for (request in Util.getSnapshot(requests)) {
            if (request.isRunning()) {
                request.pause()
                pendingRequests.add(request)
            }
        }
    }

    fun pauseAllRequests() {
        isPaused = true
        for (request in Util.getSnapshot(requests)) {
            if (request.isRunning() || request.isComplete()) {
                request.clear()
                pendingRequests.add(request)
            }
        }
    }

    fun resumeRequests() {
        isPaused = false
        for (request in Util.getSnapshot(requests)) {
            if (!request.isComplete() && !request.isRunning()) {
                request.begin()
            }
        }
        pendingRequests.clear()
    }


    fun clearAndRemove(request: Request): Boolean {
        var isOwnedByUs = requests.remove(request)
        isOwnedByUs = pendingRequests.remove(request) || isOwnedByUs
        if (isOwnedByUs) {
            request.clear()
        }
        return isOwnedByUs
    }

}