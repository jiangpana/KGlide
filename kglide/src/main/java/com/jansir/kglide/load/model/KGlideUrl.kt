package com.jansir.kglide.load.model

import com.jansir.kglide.load.Key
import java.net.URL
import java.security.MessageDigest


class KGlideUrl(val url: String) : Key {

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(url.toByteArray())
    }

    override fun equals(o: Any?): Boolean {
        if (o is KGlideUrl){
            return url == o.url
        }
        return true
    }

    override fun hashCode(): Int {
        var result = url.hashCode()

        return result
    }
}