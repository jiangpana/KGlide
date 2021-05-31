package com.jansir.kglide.request


interface Request {
    fun begin()
    fun clear()
    fun isRunning(): Boolean
    fun isComplete(): Boolean
    fun isResourceSet(): Boolean
    fun isCleared(): Boolean
    fun isFailed(): Boolean
    fun recycle(): Boolean
    fun isEquivalentTo(request: Request): Boolean
}