package com.jansir.kglide.manager


interface ConnectivityMonitorFactory {
    fun build():ConnectivityMonitor
}