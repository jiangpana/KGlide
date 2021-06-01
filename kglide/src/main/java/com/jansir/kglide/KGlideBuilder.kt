package com.jansir.kglide

import com.jansir.kglide.load.engine.exector.GlideExecutor
import com.jansir.kglide.manager.ConnectivityMonitorFactory

class KGlideBuilder {

    internal fun build(): KGlide {

        return KGlide()
    }

    private lateinit var connectivityMonitorFactory: ConnectivityMonitorFactory
    private lateinit var animationExecutor: GlideExecutor
    private lateinit var sourceExecutor: GlideExecutor
    private lateinit var diskCacheExecutor: GlideExecutor

}