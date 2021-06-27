package com.jansir.kglide.manager

import android.content.Context


interface ConnectivityMonitorFactory {
    fun build(context:Context,listener:ConnectivityMonitor.ConnectivityListener):ConnectivityMonitor
}