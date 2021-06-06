package com.jansir.kglide.load

import java.security.MessageDigest


class Options:Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun equals(o: Any?): Boolean {
   return true
    }

    override fun hashCode(): Int {
        return 1
    }
}