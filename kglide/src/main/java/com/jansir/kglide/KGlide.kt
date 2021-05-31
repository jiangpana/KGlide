package com.jansir.kglide

import android.app.Activity
import android.app.FragmentManager
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.jansir.kglide.ext.assertNotDestroyed
import com.jansir.kglide.ext.isActivityVisible
import com.jansir.kglide.ext.isOnMainThread


object KGlide {

    init {


    }

    fun with(activity: Activity): RequestManager {
        if (isOnMainThread()) {
            activity.assertNotDestroyed()

            return fragmentGet(
                activity,
                activity.fragmentManager,
                null,
                activity.isActivityVisible()
            )
        }
        return RequestManager()
    }

    private fun fragmentGet(activity: Activity, fragmentManager: FragmentManager, parentHint: Fragment?, isParentVisible: Boolean): RequestManager {

        return RequestManager()
    }

/*    fun with(context: Context) :RequestManager{

    }

    fun with(view: View) :RequestManager{

    }

    fun with(view: Fragment) :RequestManager{

    }*/
}