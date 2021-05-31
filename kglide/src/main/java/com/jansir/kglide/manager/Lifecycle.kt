package com.jansir.kglide.manager


interface Lifecycle {
    fun addListener(listener :LifecycleListener)
    fun removeListener(listener :LifecycleListener)
}