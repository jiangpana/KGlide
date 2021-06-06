package com.jansir.kglide.load

import android.content.Context
import com.jansir.kglide.load.engine.Resource


interface Transformation<T> :Key {
    fun transform(
        context: Context, resource: Resource<T>, outWidth: Int, outHeight: Int
    ): Resource<T>?
}