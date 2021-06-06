package com.jansir.kglide.load.engine.cache

import com.jansir.kglide.load.Key
import java.io.File

interface DiskCache {
    /** An interface for lazily creating a disk cache.  */
    interface Factory {
        /** Returns a new disk cache, or `null` if no disk cache could be created.  */
        fun build(): DiskCache

        companion object {
            /** 250 MB of cache.  */
            const val DEFAULT_DISK_CACHE_SIZE = 250 * 1024 * 1024
            const val DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache"
        }
    }

    /** An interface to actually write data to a key in the disk cache.  */
    interface Writer {
        /**
         * Writes data to the file and returns true if the write was successful and should be committed,
         * and false if the write should be aborted.
         *
         * @param file The File the Writer should write to.
         */
        fun write(file: File): Boolean
    }

    /**
     * Get the cache for the value at the given key.
     *
     *
     * Note - This is potentially dangerous, someone may write a new value to the file at any point
     * in time and we won't know about it.
     *
     * @param key The key in the cache.
     * @return An InputStream representing the data at key at the time get is called.
     */
    operator fun get(key: Key): File?

    /**
     * Write to a key in the cache. [Writer] is used so that the cache implementation can
     * perform actions after the write finishes, like commit (via atomic file rename).
     *
     * @param key The key to write to.
     * @param writer An interface that will write data given an OutputStream for the key.
     */
    fun put(key: Key?, writer: Writer)

    /**
     * Remove the key and value from the cache.
     *
     * @param key The key to remove.
     */
    // Public API.
    fun delete(key: Key)

    /** Clear the cache.  */
    fun clear()
}