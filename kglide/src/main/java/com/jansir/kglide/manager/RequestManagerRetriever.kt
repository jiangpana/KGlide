package com.jansir.kglide.manager

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.jansir.kglide.KGlide
import com.jansir.kglide.RequestManager
import com.jansir.kglide.ext.isActivityVisible
import com.jansir.kglide.ext.isOnMainThread
import java.util.*

class RequestManagerRetriever : Handler.Callback {

    companion object {
        const val FRAGMENT_TAG = "com.bumptech.glide.manager"
        private const val TAG = "RMRetriever"
        private const val ID_REMOVE_FRAGMENT_MANAGER = 1
        private const val ID_REMOVE_SUPPORT_FRAGMENT_MANAGER = 2
    }

    private val factory by lazy {
        DEFAULT_FACTORY
    }

    private val handler by lazy {
        Handler(Looper.getMainLooper(), this /* Callback */);
    }

    override fun handleMessage(msg: Message): Boolean {
        val handled = true
        return handled
    }

    fun get(context: Context): RequestManager {
        requireNotNull(context) { "You cannot start a load on a null Context" }
        if (isOnMainThread() && context !is Application) {
            if (context is FragmentActivity) {
                return get(context)
            } else if (context is Activity) {
                return get(context)
            } else if (context is ContextWrapper
                // Only unwrap a ContextWrapper if the baseContext has a non-null application context.
                // Context#createPackageContext may return a Context without an Application instance,
                // in which case a ContextWrapper may be used to attach one.
                && context.baseContext
                    .applicationContext != null
            ) {
                return get(context.baseContext)
            }
        }
        return getApplicationManager(context)
    }

    @Volatile
    private var applicationManager: RequestManager? = null

    private fun getApplicationManager(context: Context): RequestManager {
        if (applicationManager == null) {
            synchronized(this) {
                if (applicationManager == null) {
                    // Normally pause/resume is taken care of by the fragment we add to the fragment or
                    // activity. However, in this case since the manager attached to the application will not
                    // receive lifecycle events, we must force the manager to start resumed using
                    // ApplicationLifecycle.
                    // TODO(b/27524013): Factor out this Glide.get() call.
                    val glide = KGlide.get(context.applicationContext)
                    applicationManager = factory.build(
                        glide,
                        ApplicationLifecycle(),
                        EmptyRequestManagerTreeNode(),
                        context.applicationContext
                    )
                }
            }
        }
        return applicationManager!!
    }

    fun get(activity: FragmentActivity): RequestManager {
        if (!isOnMainThread()) {
            return get(activity.applicationContext)
        } else {
            val fm = activity.supportFragmentManager
            return supportFragmentGet(activity, fm, null, activity.isActivityVisible())
        }
    }

    private fun supportFragmentGet(
        context: Context,
        fm: FragmentManager,
        parentHint: Fragment?,
        isParentVisible: Boolean
    ): RequestManager {
        val current = getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
        var requestManager = current.getRequestManager()
        if (requestManager == null) {
            // TODO(b/27524013): Factor out this Glide.get() call.
            val glide = KGlide.get(context)
            requestManager = factory.build(
                glide, current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context
            )
            current.setRequestManager(requestManager)
        }
        return requestManager
    }

    val pendingSupportRequestManagerFragments: HashMap<FragmentManager, SupportRequestManagerFragment> =
        HashMap()

    private fun getSupportRequestManagerFragment(
        fm: FragmentManager,
        parentHint: Fragment?,
        isParentVisible: Boolean
    ): SupportRequestManagerFragment {
        var current = fm.findFragmentByTag(FRAGMENT_TAG)
        if (current == null) {
            current = pendingSupportRequestManagerFragments.get(fm)
            if (current == null) {
                current = SupportRequestManagerFragment()
                current.setParentFragmentHint(parentHint)
                if (isParentVisible) {
                    current.getGlideLifecycle().onStart()
                }
                pendingSupportRequestManagerFragments[fm] = current
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss()
            }
        }
        return current as SupportRequestManagerFragment
    }

    interface RequestManagerFactory {

        fun build(
            kGlide: KGlide,
            lifecycle: Lifecycle,
            requestManagerTreeNode: RequestManagerTreeNode,
            context: Context
        ): RequestManager
    }

    private val DEFAULT_FACTORY = object : RequestManagerFactory {
        override fun build(
            kGlide: KGlide,
            lifecycle: Lifecycle,
            requestManagerTreeNode: RequestManagerTreeNode,
            context: Context
        ): RequestManager {
            return RequestManager(kGlide, lifecycle, requestManagerTreeNode, context);
        }
    }
}