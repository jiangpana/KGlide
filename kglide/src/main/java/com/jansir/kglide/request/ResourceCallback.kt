package com.jansir.kglide.request

import com.jansir.kglide.load.engine.Resource
import javax.sql.DataSource

interface ResourceCallback {
    fun onResourceReady(resource: Resource<*>, dataSource: DataSource?)
}