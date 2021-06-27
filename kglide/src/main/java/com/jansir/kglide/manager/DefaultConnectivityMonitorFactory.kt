package com.jansir.kglide.manager

import android.content.Context

class DefaultConnectivityMonitorFactory :ConnectivityMonitorFactory {
    override fun build(
        context: Context,
        listener: ConnectivityMonitor.ConnectivityListener
    ): ConnectivityMonitor {
        return DefaultConnectivityMonitor(context,listener)
    }
}