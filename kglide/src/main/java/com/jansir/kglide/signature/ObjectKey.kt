package com.jansir.kglide.signature

import com.jansir.kglide.load.Key
import java.security.MessageDigest

class ObjectKey(val obj :Any) :Key {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(obj.toString().toByte())
    }

    override fun equals(o: Any?): Boolean {
        if (o is ObjectKey){
            return o.obj == obj
        }
        return false
    }

    override fun hashCode(): Int {
        var result =obj.hashCode()
        return  result
    }
}