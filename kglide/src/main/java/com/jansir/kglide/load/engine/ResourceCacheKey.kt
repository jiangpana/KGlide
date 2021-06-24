package com.jansir.kglide.load.engine

import com.jansir.kglide.load.Key
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.Transformation
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.util.LruCache
import java.security.MessageDigest

class ResourceCacheKey(
    val arrayPool: ArrayPool,
    val sourceKey: Key,
    val signature: Key,
    val width: Int,
    val height: Int,
    val appliedTransformation: Transformation<*>? = null,
    val decodedResourceClass: Class<*>,
    val options: Options
) : Key {
    val RESOURCE_CLASS_BYTES = LruCache<Class<*>, ByteArray>(50);
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        signature.updateDiskCacheKey(messageDigest)
        sourceKey.updateDiskCacheKey(messageDigest)
    }

    override fun equals(o: Any?): Boolean {
        if (o is ResourceCacheKey) {
            return o.let {
                return sourceKey == it.sourceKey &&
                        signature == it.signature &&
                        width == it.width &&
                        height == it.height &&
                        appliedTransformation == it.appliedTransformation &&
                        decodedResourceClass == it.decodedResourceClass &&
                        options == it.options
            }
        }
        return false
    }

    override fun hashCode(): Int {
        var result = sourceKey.hashCode()
        result += 31 * result + signature.hashCode()
        result += 31 * result + width
        result += 31 * result + height
        appliedTransformation?.let {
            result += 31 * result + it.hashCode()
        }
        result += 31 * result + decodedResourceClass.hashCode()
        result += 31 * result + options.hashCode()
        return result
    }
}