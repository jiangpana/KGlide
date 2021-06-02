package com.jansir.kglide.request.transition

import javax.sql.DataSource

interface TransitionFactory<in R> {
    fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<R>?
}