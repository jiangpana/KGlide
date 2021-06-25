package com.jansir.kglide.load.engine.cache

import android.util.Log
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.Key
import com.jansir.kglide.util.disklrucache.DiskLruCache
import java.io.File
import java.io.IOException

class DiskLruCacheWrapper (val directory:File , val maxSize:Long ): DiskCache {
    companion object{
        private const val TAG ="DiskLruCacheWrapper"
        private const val APP_VERSION = 1
        private const val VALUE_COUNT = 1

        fun create(directory: File, maxSize: Long): DiskCache? {
            return DiskLruCacheWrapper(directory, maxSize)
        }
    }

    private val safeKeyGenerator =SafeKeyGenerator()

    private val diskLruCache by lazy {
        DiskLruCache.open(
            directory,
            APP_VERSION,
            VALUE_COUNT,
            maxSize
        )
    }


    @Throws(IOException::class)
    private fun getDiskCache(): DiskLruCache? {
        return diskLruCache
    }

    override fun get(key: Key): File? {
        val safeKey = safeKeyGenerator.getSafeKey(key);
        printThis("get safeKey=${safeKey}")
        var result: File? = null
        // It is possible that the there will be a put in between these two gets. If so that shouldn't
        // be a problem because we will always put the same value at the same key so our input streams
        // will still represent the same data.
        try {
            val value = getDiskCache()!![safeKey]
            if (value != null) {
                result = value.getFile(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    @Synchronized
    override fun put(key: Key?, writer: DiskCache.Writer) {
        printThis("put writer=${writer.javaClass.simpleName}")
        try {
            val safeKey = safeKeyGenerator.getSafeKey(key!!);
            printThis("put safeKey=${safeKey}")
            // We assume we only need to put once, so if data was written while we were trying to get
            // the lock, we can simply abort.
            val diskCache = getDiskCache()
            if (diskCache!![safeKey] != null) {
                return
            }
            val editor = diskCache.edit(safeKey)
                ?: throw IllegalStateException("Had two simultaneous puts for: $safeKey");
            try {
                val file = editor.getFile(0)
                if (writer.write(file)) {
                    editor.commit()
                }
            } finally {
                editor.abortUnlessCommitted()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun delete(key: Key) {
        val safeKey = safeKeyGenerator.getSafeKey(key)
        try {
            getDiskCache()!!.remove(safeKey)
        } catch (e: IOException) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(TAG, "Unable to delete from disk cache", e)
            }
        }
    }

    override fun clear() {
        try {
            getDiskCache()!!.delete()
        } catch (e: IOException) {
            if (Log.isLoggable(TAG, Log.WARN)) {
                Log.w(
                    TAG,
                    "Unable to clear disk cache or disk cache cleared externally",
                    e
                )
            }
        } finally {
            // Delete can close the cache but still throw. If we don't null out the disk cache here, every
            // subsequent request will try to act on a closed disk cache and fail. By nulling out the disk
            // cache we at least allow for attempts to open the cache in the future. See #2465.
            resetDiskCache()
        }
    }

    private fun resetDiskCache() {
//        diskLruCache = null
    }
}