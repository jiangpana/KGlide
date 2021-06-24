package com.jansir.kglide.load.engine.cache

import androidx.core.util.Preconditions
import com.jansir.kglide.load.Key
import com.jansir.kglide.util.LruCache
import com.jansir.kglide.util.Util
import com.jansir.kglide.util.pool.FactoryPools
import com.jansir.kglide.util.pool.StateVerifier
import java.security.MessageDigest

class SafeKeyGenerator {
    private val loadIdToSafeHash = LruCache<Key,String>(1000)

    private val digestPool by lazy{
        FactoryPools.threadSafe(10 , object : FactoryPools.Factory<PoolableDigestContainer> {
            override fun create(): PoolableDigestContainer {
                return PoolableDigestContainer(MessageDigest.getInstance("SHA-256"));
            }
        })
    }

    fun getSafeKey(key: Key): String{
        var safeKey: String?
        synchronized(loadIdToSafeHash) { safeKey = loadIdToSafeHash[key] }
        if (safeKey == null) {
            safeKey = calculateHexStringDigest(key)
        }
        synchronized(loadIdToSafeHash) { loadIdToSafeHash.put(key, safeKey!!) }
        return safeKey!!
    }

    private fun calculateHexStringDigest(key: Key): String? {
        val container: PoolableDigestContainer = digestPool.acquire()!!
        return try {
            key.updateDiskCacheKey(container.messageDigest)
            // calling digest() will automatically reset()
            Util.sha256BytesToHex(container.messageDigest.digest())
        } finally {
            digestPool.release(container)
        }
    }

    private class PoolableDigestContainer(val messageDigest: MessageDigest): FactoryPools.Poolable {
        private val stateVerifier = StateVerifier.newInstance()
        override fun getVerifier(): StateVerifier {
            return stateVerifier
        }

    }
}