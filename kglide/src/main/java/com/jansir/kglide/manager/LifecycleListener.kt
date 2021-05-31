package com.jansir.kglide.manager


interface LifecycleListener {
    fun onStart()
    fun onStop()
    fun onDestroy()
}