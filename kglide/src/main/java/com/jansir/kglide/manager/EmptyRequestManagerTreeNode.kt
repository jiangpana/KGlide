package com.jansir.kglide.manager

import com.jansir.kglide.RequestManager

class EmptyRequestManagerTreeNode : RequestManagerTreeNode{
    override fun getDescendants(): Set<RequestManager> {
    return emptySet()
    }
}