package com.jansir.kglide.manager

import android.content.Context

class DefaultConnectivityMonitor(
    val context: Context,
    val listener: ConnectivityMonitor.ConnectivityListener
) : ConnectivityMonitor {
    override fun onStart() {
        register()
    }

    override fun onStop() {
        unregister()
    }

    private fun register() {

    }


    private fun unregister() {

    }

    override fun onDestroy() {
    }

}