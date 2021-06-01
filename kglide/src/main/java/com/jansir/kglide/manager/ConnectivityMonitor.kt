package com.jansir.kglide.manager


interface ConnectivityMonitor :LifecycleListener{
    interface ConnectivityListener{
        fun onConnectivityChanged(isConnected :Boolean)
    }
}