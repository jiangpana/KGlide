package com.jansir.kglide.manager

import com.jansir.kglide.request.target.Target
import com.jansir.kglide.util.Util
import java.util.*


class TargetTracker : LifecycleListener{

    private val targets=
        Collections.newSetFromMap(WeakHashMap<Target<*>, Boolean>())

    /*private val targets = Collections.newSetFromMap()*/
    override fun onStart() {
        for (target in Util.getSnapshot(targets)) {
            target.onStart()
        }
    }

    override fun onStop() {
        for (target in Util.getSnapshot(targets)) {
            target.onStop()
        }
    }

    override fun onDestroy() {
        for (target in Util.getSnapshot(targets)) {
            target.onDestroy()
        }
    }

    fun clear(){
        targets.clear()
    }

    fun getAll(): List<Target<*>> {
        return Util.getSnapshot(targets)
    }

    fun track(target: Target<*>) {
        targets.add(target)
    }

    fun untrack(target: Target<*>) {
        targets.remove(target)
    }
}