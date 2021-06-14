package com.jansir.kglide.load.model

import com.jansir.kglide.load.Key
import java.net.URL
import java.security.MessageDigest


class KGlideUrl(val url:String) :Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {

    }

    override fun equals(o: Any?): Boolean {
        return true
    }

    override fun hashCode(): Int {
        return  0
    }
}