package com.jansir.kglide

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.fragment.app.FragmentActivity
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.data.InputStreamRewinder
import com.jansir.kglide.load.engine.Engine
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.engine.bitmap_recycle.BitmapPool
import com.jansir.kglide.load.engine.cache.MemoryCache
import com.jansir.kglide.load.model.KGlideUrl
import com.jansir.kglide.load.model.StringLoader
import com.jansir.kglide.load.model.stream.HttpGlideUrlLoader
import com.jansir.kglide.load.model.stream.HttpUriLoader
import com.jansir.kglide.load.resource.bitmap.BitmapDrawableDecoder
import com.jansir.kglide.load.resource.bitmap.Downsampler
import com.jansir.kglide.load.resource.bitmap.StreamBitmapDecoder
import com.jansir.kglide.load.resource.transcode.BitmapDrawableTranscoder
import com.jansir.kglide.manager.ConnectivityMonitorFactory
import com.jansir.kglide.manager.RequestManagerRetriever
import com.jansir.kglide.request.RequestOptions
import java.io.InputStream


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
    private var glideContext = GlideContext(context)

    init {
        val resources =context.resources
        glideContext.getRegistry().apply {
            //model
            append(String::class.java, InputStream::class.java, StringLoader.StreamFactory())
            append(Uri::class.java, InputStream::class.java, HttpUriLoader.Factory())
            append(KGlideUrl::class.java, InputStream::class.java, HttpGlideUrlLoader.Factory())

            //decode
            val streamBitmapDecoder: ResourceDecoder<InputStream, Bitmap>
            streamBitmapDecoder = StreamBitmapDecoder(
                downsampler = Downsampler(bitmapPool, byteArrayPool = arrayPool),
                byteArrayPool = arrayPool
            )
            append(
                Registry.BUCKET_BITMAP,
                InputStream::class.java,
                Bitmap::class.java,
                streamBitmapDecoder
            )
            append(
                    Registry.BUCKET_BITMAP_DRAWABLE,
                    InputStream::class.java,
                            BitmapDrawable::class.java,
             BitmapDrawableDecoder(resources, streamBitmapDecoder)
            )

            //transcode
            register(Bitmap::class.java,BitmapDrawable::class.java,
                BitmapDrawableTranscoder(resources)
            )
            register(InputStreamRewinder.Factory(arrayPool))
        }
    }

    fun getGlideContext(): GlideContext {
        return glideContext
    }

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