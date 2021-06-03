package com.jansir.kglide.request


interface Request {
    fun begin()
    fun clear()
    fun pause()
    fun isRunning(): Boolean
    fun isComplete(): Boolean
    fun isAnyResourceSet(): Boolean
    fun isCleared(): Boolean
    fun isEquivalentTo(request: Request): Boolean
}