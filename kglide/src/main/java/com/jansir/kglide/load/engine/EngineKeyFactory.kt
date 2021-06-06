package com.jansir.kglide.load.engine

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation

class EngineKeyFactory {

    fun buildKey(
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        transformations: Map<Class<*>, Transformation<*>>,
        resourceClass: Class<*>,
        transcodeClass: Class<*>,
        options: Options
    ): EngineKey {
        return EngineKey(
            model,
            signature,
            width,
            height,
            transformations,
            resourceClass,
            transcodeClass,
            options
        )
    }
}