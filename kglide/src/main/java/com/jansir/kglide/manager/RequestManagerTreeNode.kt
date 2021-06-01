package com.jansir.kglide.manager

import com.jansir.kglide.RequestManager

interface RequestManagerTreeNode {
    fun getDescendants(): Set<RequestManager>
}