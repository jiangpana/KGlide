package com.jansir.kglide.load.key

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.engine.EngineJob
import com.jansir.kglide.load.engine.EngineResource


interface EngineJobListener {
    fun onEngineJobComplete(
        engineJob: EngineJob<*>?,
        key: Key?,
        resource: EngineResource<*>?
    )

    fun onEngineJobCancelled(engineJob: EngineJob<*>?, key: Key?)
}