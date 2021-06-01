package com.jansir.kglide

import android.content.Context
import com.jansir.kglide.manager.Lifecycle
import com.jansir.kglide.manager.RequestManagerTreeNode


class RequestManager(
    val kGlide: KGlide,
    val lifecycle: Lifecycle,
    val requestManagerTreeNode: RequestManagerTreeNode,
    val context: Context
) {
}