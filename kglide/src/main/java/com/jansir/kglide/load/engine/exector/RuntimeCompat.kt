package com.jansir.kglide.load.engine.exector

import android.os.Build
import android.os.StrictMode
import android.util.Log
import java.io.File
import java.util.regex.Pattern

object RuntimeCompat {
    private const val TAG = "GlideRuntimeCompat"
    private const val CPU_NAME_REGEX = "cpu[0-9]+"
    private const val CPU_LOCATION = "/sys/devices/system/cpu/"

    /** Determines the number of cores available on the device.  */
    fun availableProcessors(): Int {
        var cpus = Runtime.getRuntime().availableProcessors()
        if (Build.VERSION.SDK_INT < 17) {
            cpus = Math.max(getCoreCountPre17(), cpus)
        }
        return cpus
    }

    /**
     * Determines the number of cores available on the device (pre-v17).
     *
     *
     * Before Jellybean, [Runtime.availableProcessors] returned the number of awake cores,
     * which may not be the number of available cores depending on the device's current state. See
     * https://stackoverflow.com/a/30150409.
     *
     * @return the maximum number of processors available to the VM; never smaller than one
     */
    private fun getCoreCountPre17(): Int {
        // We override the current ThreadPolicy to allow disk reads.
        // This shouldn't actually do disk-IO and accesses a device file.
        // See: https://github.com/bumptech/glide/issues/1170
        var cpus: Array<File?>? = null
        val originalPolicy = StrictMode.allowThreadDiskReads()
        try {
            val cpuInfo = File(CPU_LOCATION)
            val cpuNamePattern =
                Pattern.compile(CPU_NAME_REGEX)
            cpus = cpuInfo.listFiles { file, s -> cpuNamePattern.matcher(s).matches() }
        } catch (t: Throwable) {
            if (Log.isLoggable(TAG, Log.ERROR)) {
                Log.e(TAG, "Failed to calculate accurate cpu count", t)
            }
        } finally {
            StrictMode.setThreadPolicy(originalPolicy)
        }
        return Math.max(1, cpus?.size ?: 0)
    }
}