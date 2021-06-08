package com.jansir.kglide.request.target

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.view.*
import com.jansir.kglide.request.Request
import com.jansir.kglide.request.target.Target.Companion.SIZE_ORIGINAL
import com.jansir.kglide.request.transition.Transition
import java.lang.ref.WeakReference
import java.util.*

abstract class ViewTarget<T : View, Z>(override val view: T) : Target<Z>, Transition.ViewAdapter {

    private var request: Request? = null

     val sizeDeterminer by lazy {
        SizeDeterminer(view)
    }


    override fun setRequest(request: Request?) {
        this.request = request
    }

    fun waitForLayout(): ViewTarget<T, Z> {
        sizeDeterminer.waitForLayout = true
        return this
    }

    override fun getRequest(): Request? {
        return request
    }

    override fun onLoadStarted(placeholder: Drawable?) {
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
    }

    override fun onResourceReady(resource: Z, transition: Transition<Z?>?) {

    }

    override fun onLoadCleared(placeholder: Drawable?) {
    }

    override fun getSize(cb: SizeReadyCallback) {
        return sizeDeterminer.getSize(cb)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onDestroy() {
    }

    class SizeDeterminer(private val view: View) {
        companion object {
            val PENDING_SIZE = 0
        }

        var maxDisplayLength: Int? = null
        var waitForLayout = false
        private val cbs = ArrayList<SizeReadyCallback>()
        private var layoutListener: SizeDeterminerLayoutListener? = null

        private fun checkCurrentDimens() {
            if (cbs.isEmpty()) {
                return
            }
            val currentWidth = getTargetWidth()
            val currentHeight = getTargetHeight()
            if (!isViewStateAndSizeValid(currentWidth, currentHeight)) {
                return
            }
            notifyCbs(currentWidth, currentHeight)
            clearCallbacksAndListener()
        }

        private fun clearCallbacksAndListener() {
            val observer = view.getViewTreeObserver();
            if (observer.isAlive) {
                observer.removeOnPreDrawListener(layoutListener)
            }
            layoutListener = null
            cbs.clear()
        }

        private fun notifyCbs(width: Int, height: Int) {
            // One or more callbacks may trigger the removal of one or more additional callbacks, so we
            // need a copy of the list to avoid a concurrent modification exception. One place this
            // happens is when a full request completes from the in memory cache while its thumbnail is
            // still being loaded asynchronously. See #2237.
            for (cb in ArrayList<SizeReadyCallback>(cbs)) {
                cb.onSizeReady(width, height)
            }
        }

        private fun isDimensionValid(size: Int): Boolean {
            return size > 0 || size == SIZE_ORIGINAL
        }

        fun getSize(cb: SizeReadyCallback) {
            val currentWidth: Int = getTargetWidth()
            val currentHeight: Int = getTargetHeight()
            if (isViewStateAndSizeValid(currentWidth, currentHeight)) {
                cb.onSizeReady(currentWidth, currentHeight)
                return
            }
            if (!cbs.contains(cb)) {
                cbs.add(cb)
            }
            if (layoutListener == null) {
                val observer = view.viewTreeObserver
                layoutListener = SizeDeterminerLayoutListener(this)
                observer.addOnPreDrawListener(layoutListener)
            }
        }

        private fun getTargetHeight(): Int {
            val verticalPadding = view.paddingTop + view.paddingBottom
            val layoutParams = view.layoutParams
            val layoutParamSize =
                layoutParams?.height ?: PENDING_SIZE
            return getTargetDimen(view.height, layoutParamSize, verticalPadding)
        }

        private fun isViewStateAndSizeValid(width: Int, height: Int): Boolean {
            return isDimensionValid(width) && isDimensionValid(height)
        }

        private fun getTargetWidth(): Int {
            val horizontalPadding = view.paddingLeft + view.paddingRight;
            val layoutParams = view.layoutParams
            val layoutParamSize =
                layoutParams?.let { layoutParams.width } ?: PENDING_SIZE
            return getTargetDimen(view.getWidth(), layoutParamSize, horizontalPadding)
        }

        private fun getTargetDimen(viewSize: Int, paramSize: Int, paddingSize: Int): Int {
            val adjustedParamSize = paramSize - paddingSize;
            if (adjustedParamSize > 0) {
                return adjustedParamSize
            }
            if (waitForLayout && view.isLayoutRequested) {
                return PENDING_SIZE
            }
            val adjustedViewSize: Int = viewSize - paddingSize
            if (adjustedViewSize > 0) {
                return adjustedViewSize
            }
            if (!view.isLayoutRequested && paramSize == ViewGroup.LayoutParams.WRAP_CONTENT) {
                return getMaxDisplayLength(view.context)
            }
            return PENDING_SIZE
        }

        private fun getMaxDisplayLength(context: Context): Int {
            if (maxDisplayLength == null) {
                val windowManager =
                    context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display: Display = windowManager.getDefaultDisplay()
                val displayDimensions = Point()
                display.getSize(displayDimensions)
                maxDisplayLength =
                    Math.max(displayDimensions.x, displayDimensions.y)
            }
            return maxDisplayLength as Int
        }

        class SizeDeterminerLayoutListener(sizeDeterminer: SizeDeterminer) :
            ViewTreeObserver.OnPreDrawListener {
            private val sizeDeterminerRef = WeakReference(sizeDeterminer)

            override fun onPreDraw(): Boolean {
                val sizeDeterminer = sizeDeterminerRef.get()
                sizeDeterminer?.checkCurrentDimens()
                return true
            }

        }


    }


}