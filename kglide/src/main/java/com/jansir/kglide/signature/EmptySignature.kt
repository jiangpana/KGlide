package com.jansir.kglide.signature

import com.jansir.kglide.load.Key
import java.security.MessageDigest


class EmptySignature :Key {
    companion object{
        private val EMPTY_KEY = EmptySignature()
        fun obtain(): EmptySignature {
            return EMPTY_KEY
        }
    }
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun equals(o: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return  0
    }


}