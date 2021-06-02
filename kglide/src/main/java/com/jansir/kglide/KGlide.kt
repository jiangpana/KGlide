package com.jansir.kglide

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.jansir.kglide.load.engine.Engine
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.engine.bitmap_recycle.BitmapPool
import com.jansir.kglide.load.engine.cache.MemoryCache
import com.jansir.kglide.manager.ConnectivityMonitorFactory
import com.jansir.kglide.manager.RequestManagerRetriever
import com.jansir.kglide.request.RequestOptions


class KGlide(
    val context: Context,
    val engine: Engine,
    val memoryCache: MemoryCache,
    val requestManagerRetriever: RequestManagerRetriever,
    val connectivityMonitorFactory: ConnectivityMonitorFactory,
    val bitmapPool: BitmapPool,
    val arrayPool: ArrayPool,
    val requestOptionsFactory: RequestOptionsFactory
) {

    companion object {
        private var instance: KGlide? = null

        @Synchronized
        fun get(context: Context): KGlide {
            if (instance == null) {
                synchronized(KGlide::class.java) {
                    if (instance == null) {
                        checkAndInitializeGlide(context)
                    }
                }
            }
            return instance!!
        }

        fun with(activity: FragmentActivity): RequestManager {
            return get(activity).requestManagerRetriever.get(activity)
        }

        private fun checkAndInitializeGlide(context: Context) {
            val builder = KGlideBuilder()
            instance = builder.build(context)
        }
    }


    /** Creates a new instance of [RequestOptions].  */
    interface RequestOptionsFactory {
        /** Returns a non-null [RequestOptions] object.  */
        fun build(): RequestOptions
    }
}