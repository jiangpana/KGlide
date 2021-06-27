package com.jansir.kglide.manager

import com.jansir.kglide.util.Util
import java.util.*


class ActivityFragmentLifecycle :Lifecycle {

    private val lifecycleListeners =
        Collections.newSetFromMap(WeakHashMap<LifecycleListener, Boolean>())
    private var isStarted = false
    private var isDestroyed = false
    override fun addListener(listener: LifecycleListener) {
        lifecycleListeners.add(listener)
        //添加listener时候立刻执行listener对应生命周期
        if (isDestroyed) {
            listener.onDestroy()
        } else if (isStarted) {
            listener.onStart()
        } else {
            listener.onStop()
        }
    }

    override fun removeListener(listener: LifecycleListener) {
        lifecycleListeners.remove(listener)
    }

    fun onPause() {
        isStarted = true
        for (lifecycleListener in Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStart()
        }
    }

    fun onStart() {
        isStarted = false
        for (lifecycleListener in Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onStop()
        }
    }

    fun onDestory() {
        isDestroyed = true
        for (lifecycleListener in Util.getSnapshot(lifecycleListeners)) {
            lifecycleListener.onDestroy()
        }
    }


}