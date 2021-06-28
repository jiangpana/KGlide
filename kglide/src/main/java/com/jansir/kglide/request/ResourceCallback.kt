package com.jansir.kglide.request

import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.engine.Resource

interface ResourceCallback {
    fun onResourceReady(resource: Resource<*>, dataSource: DataSource?)
    fun onLoadFailed(e: Exception)
    fun getLock(): Any
}