package com.jansir.kglide.manager

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class DefaultConnectivityMonitorFactory : ConnectivityMonitorFactory {
    companion object {
        private const val NETWORK_PERMISSION = "android.permission.ACCESS_NETWORK_STATE"
    }

    override fun build(
        context: Context,
        listener: ConnectivityMonitor.ConnectivityListener
    ): ConnectivityMonitor {
        val permissionResult = ContextCompat.checkSelfPermission(
            context,
            NETWORK_PERMISSION
        )
        val hasPermission = permissionResult == PackageManager.PERMISSION_GRANTED
        return if (hasPermission) DefaultConnectivityMonitor(
            context,
            listener
        ) else NullConnectivityMonitor()
    }
}