package com.jansir.kglide.manager


interface ConnectivityMonitor :Lifecycle{
    interface ConnectivityListener{
        fun onConnectivityChanged(isConnected :Boolean)
    }
}