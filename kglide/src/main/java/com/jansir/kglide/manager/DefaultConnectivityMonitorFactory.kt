package com.jansir.kglide.manager

class DefaultConnectivityMonitorFactory :ConnectivityMonitorFactory {
    override fun build(): ConnectivityMonitor {
        return DefaultConnectivityMonitor()
    }
}