package com.jansir.kglide.request

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.jansir.kglide.GlideContext
import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.engine.Engine
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.request.target.ImageViewTarget
import com.jansir.kglide.request.target.SizeReadyCallback
import com.jansir.kglide.request.target.Target
import com.jansir.kglide.request.target.Target.Companion.SIZE_ORIGINAL
import com.jansir.kglide.request.transition.TransitionFactory
import java.util.concurrent.Executor
import kotlin.math.roundToInt

class SingleRequest<R> private constructor(
    val context: Context,
    val glideContext: GlideContext,
    val model: Any, val transcodeClass: Class<R>, val requestOptions: BaseRequestOptions<*>,
    val overrideWidth: Int, val overrideHeight: Int, val priority: Priority,
    val target: Target<R>,
    val targetListener: RequestListener<R>? = null,
    val requestListeners: List<RequestListener<R>>? = null,
    val requestCoordinator: RequestCoordinator? = null,
    val engine: Engine,
    val animationFactory: TransitionFactory<R>? = null,
    val callbackExecutor: Executor
) : Request, SizeReadyCallback,ResourceCallback {

    private val requestLock: Any = Any()
    private var status: Status = Status.PENDING
    private var loadStatus: Engine.LoadStatus? = null

    companion object {
        fun <R> obtain(
            context: Context,
            glideContext: GlideContext,
            model: Any,
            transcodeClass: Class<R>,
            requestOptions: BaseRequestOptions<*>,
            overrideWidth: Int,
            overrideHeight: Int,
            priority: Priority,
            target: Target<R>,
            targetListener: RequestListener<R>? = null,
            requestListeners: List<RequestListener<R>>? = null,
            requestCoordinator: RequestCoordinator? = null,
            engine: Engine,
            animationFactory: TransitionFactory<R>? = null,
            callbackExecutor: Executor
        ): SingleRequest<R> {
            return SingleRequest(
                context,
                glideContext,
                model,
                transcodeClass,
                requestOptions,
                overrideWidth,
                overrideHeight,
                priority,
                target,
                targetListener,
                requestListeners,
                requestCoordinator,
                engine,
                animationFactory,
                callbackExecutor
            )

        }
    }

    sealed class Status {
        object PENDING : Status()
        object RUNNING : Status()
        object WAITING_FOR_SIZE : Status()
        object COMPLETE : Status()
        object FAILED : Status()
        object CLEARED : Status()
    }

    private var width = 0
    private var height = 0
    override fun begin() {
        if (model == null) {
            if (isValidDimensions(overrideWidth, overrideHeight)) {
                width = overrideWidth
                height = overrideHeight
            }
            onLoadFailed(Exception("Received null model"), Log.WARN)
            return
        }
        status = Status.WAITING_FOR_SIZE
        if(isValidDimensions(overrideWidth, overrideHeight)){
            onSizeReady(overrideWidth,overrideHeight)
        }else{
            target.getSize(this)
        }
        if ((status == Status.RUNNING || status == Status.WAITING_FOR_SIZE)
            && canNotifyStatusChanged()
        ) {
            target.onLoadStarted(getPlaceholderDrawable())
        }
    }

    private fun getPlaceholderDrawable(): Drawable? {
        return null
    }

    private fun canNotifyStatusChanged(): Boolean {
        return true
    }

    private fun onLoadFailed(exception: Exception, logLevel: Int) {
        synchronized(requestLock) {

        }
    }

    private fun isValidDimensions(width: Int, height: Int): Boolean {
        return isValidDimension(width) && isValidDimension(height)
    }

    private fun isValidDimension(dimen: Int): Boolean {
        return dimen > 0 || dimen == SIZE_ORIGINAL

    }

    override fun clear() {
    }

    override fun pause() {
        synchronized(requestLock) {
            if (isRunning()) {
                clear()
            }
        }
    }

    override fun isRunning(): Boolean {
        synchronized(requestLock) {
            return status == Status.RUNNING || status == Status.WAITING_FOR_SIZE
        }
    }

    override fun isComplete(): Boolean {
        synchronized(requestLock) { return status == Status.COMPLETE }
    }

    override fun isAnyResourceSet(): Boolean {
        synchronized(requestLock) { return status == Status.COMPLETE }
    }

    override fun isCleared(): Boolean {
        synchronized(requestLock) { return status == Status.CLEARED }
    }

    override fun isEquivalentTo(request: Request): Boolean {
        return false
    }

    override fun onSizeReady(width: Int, height: Int) {
        if (status != Status.WAITING_FOR_SIZE) {
            return
        }
        println("onSizeReady ==${(target as ImageViewTarget<*>).view.id} width -> $width height ->$height")
        val sizeMultiplier: Float = requestOptions.getSizeMultiplier()

        this.width = maybeApplySizeMultiplier(width, sizeMultiplier)
        this.height = maybeApplySizeMultiplier(height, sizeMultiplier)
        status = Status.RUNNING
        loadStatus = engine.load(
            glideContext,
            model,
            requestOptions.getSignature(),
            this.width,
            this.height,
            requestOptions.getResourceClass(),
            transcodeClass,
            priority,
            requestOptions.getDiskCacheStrategy(),
            requestOptions.getTransformations(),
            requestOptions.isTransformationRequired(),
            requestOptions.isScaleOnlyOrNoTransform(),
            requestOptions.getOptions(),
            requestOptions.isMemoryCacheable(),
            requestOptions.getUseUnlimitedSourceGeneratorsPool(),
            requestOptions.getUseAnimationPool(),
            requestOptions.getOnlyRetrieveFromCache(),
            this,
            callbackExecutor
        )
    }

    private fun maybeApplySizeMultiplier(size: Int, sizeMultiplier: Float): Int {
        return if (size == SIZE_ORIGINAL) size else (sizeMultiplier * size).roundToInt()
    }

    override fun onResourceReady(resource: Resource<*>, dataSource: DataSource?) {
    }

    override fun onLoadFailed(e: Exception) {
    }


}