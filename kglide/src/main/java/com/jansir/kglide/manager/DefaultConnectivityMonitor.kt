package com.jansir.kglide.manager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo

class DefaultConnectivityMonitor(
    val context: Context,
    val listener: ConnectivityMonitor.ConnectivityListener
) : ConnectivityMonitor {


    var isConnected = false
    private var isRegistered = false

    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val wasConnected = isConnected
            isConnected = isConnected(context!!)
            if (wasConnected != isConnected) {
                listener.onConnectivityChanged(isConnected)
            }
        }
    }

    fun isConnected(context: Context): Boolean {
        val connectivityManager =  context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo
        networkInfo = try {
            connectivityManager.activeNetworkInfo!!
        } catch (e: RuntimeException) {
            // Default to true;
            return true
        }
        return networkInfo.isConnected
    }


    private fun register() {
        if (isRegistered) {
            return
        }
        // Initialize isConnected.
        isConnected = isConnected(context)
        try {
            context.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
            isRegistered=true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun unregister() {
        if (!isRegistered) {
            return
        }
        context.unregisterReceiver(connectivityReceiver)
        isRegistered = false
    }

    override fun onStart() {
        register()
    }

    override fun onStop() {
        unregister()
    }

    override fun onDestroy() {
    }

}