package com.jansir.kglide

import android.content.Context
import android.widget.ImageView
import com.jansir.kglide.ext.isOnMainThread
import com.jansir.kglide.request.BaseRequestOptions
import com.jansir.kglide.request.Request
import com.jansir.kglide.request.RequestListener
import com.jansir.kglide.request.target.Target
import com.jansir.kglide.request.target.ViewTarget
import com.jansir.kglide.util.Executors
import java.util.concurrent.Executor

class RequestBuilder<TranscodeType>(
    kGlide: KGlide,
    requestManager: RequestManager, val transcodeClass: Class<TranscodeType>, context: Context
) : BaseRequestOptions<RequestBuilder<TranscodeType>>(),
    ModelTypes<RequestBuilder<TranscodeType>> {

    private var glideContext: GlideContext = kGlide.getGlideContext()
    var isModelSet = false
    lateinit var model: Any

    override fun load(string: String): RequestBuilder<TranscodeType> {
        model = string
        isModelSet = true
        return this
    }

    fun into(view: ImageView): ViewTarget<ImageView, TranscodeType> {
        require(isOnMainThread()){"must load on main thread"}
        val requestOptions: BaseRequestOptions<*> = this
        if (!requestOptions.isTransformationSet() && requestOptions.isTransformationAllowed()
            && view.scaleType != null
        ) {
            when (view.scaleType) {
                ImageView.ScaleType.CENTER_CROP -> {
                }
                ImageView.ScaleType.CENTER_INSIDE -> {
                }
                ImageView.ScaleType.FIT_XY -> {
                }
                ImageView.ScaleType.FIT_CENTER,
                ImageView.ScaleType.FIT_START,
                ImageView.ScaleType.FIT_END
                -> {
                }
            }
        }
        return into(
            glideContext.buildImageViewTarget(view, transcodeClass), null, requestOptions,
            Executors.mainThreadExecutor()
        )
    }

    private fun <Y : Target<TranscodeType>> into(
        target: Y,
        targetListener: RequestListener<TranscodeType>?,
        options: BaseRequestOptions<*>,
        callbackExecutor: Executor
    ): Y {
        require(isModelSet) { "You must call #load() before calling #into()" }
//        val request = buildRequest(target, targetListener, options, callbackExecutor);
        return target
    }

  /*  private fun <Y> buildRequest(
        target: Y,
        targetListener: RequestListener<TranscodeType>?,
        options: BaseRequestOptions<*>,
        callbackExecutor: Executor
    ): Request {
        return SingleRequest
    }*/
}