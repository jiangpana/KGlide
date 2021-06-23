package com.jansir.kglide.load.engine

import com.jansir.kglide.load.Key
import java.security.MessageDigest

class DataCacheKey(val sourceKey:Key ,val signature:Key): Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        sourceKey.updateDiskCacheKey(messageDigest)
        signature.updateDiskCacheKey(messageDigest)
    }

    override fun equals(o: Any?): Boolean {
        if ( o is DataCacheKey){
            return (sourceKey==o.sourceKey) && (signature==o.signature)
        }
        return false
    }

    override fun hashCode(): Int {
        var result = sourceKey.hashCode()
        result+=result*31+signature.hashCode()
        return result
    }

}