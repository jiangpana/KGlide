package com.jansir.kglide.manager

import androidx.fragment.app.Fragment
import com.jansir.kglide.RequestManager
import com.jansir.kglide.ext.printThis

class SupportRequestManagerFragment constructor(var lifecycle: ActivityFragmentLifecycle = ActivityFragmentLifecycle()) :
    Fragment() {

    companion object {
        private const val TAG = "SupportRMFragment"
    }


    override fun onStop() {
        super.onStop()
        printThis("onStop")
        lifecycle.onStop()
    }

    override fun onStart() {
        super.onStart()
        printThis("onStart")
        lifecycle.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()
        printThis("onDestroy")
        lifecycle.onDestory()
    }

    //todo 1
    fun setParentFragmentHint(parentHint: Fragment?) {
        parentHint?.let {

        }
    }

    fun getGlideLifecycle(): ActivityFragmentLifecycle {
        return lifecycle
    }

    private var requestManager: RequestManager? = null

    fun getRequestManager(): RequestManager? {
        return requestManager
    }

    fun setRequestManager(requestManager: RequestManager) {
        this.requestManager = requestManager
    }

    fun getRequestManagerTreeNode(): RequestManagerTreeNode {
        return requestManagerTreeNode
    }

    private val requestManagerTreeNode: RequestManagerTreeNode =
        SupportFragmentRequestManagerTreeNode()

    inner class SupportFragmentRequestManagerTreeNode : RequestManagerTreeNode {
        override fun getDescendants(): Set<RequestManager> {
            return emptySet()
        }

    }
}