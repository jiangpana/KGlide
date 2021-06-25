package com.jansir.kglide.load.engine

import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.EncodeStrategy


abstract class DiskCacheStrategy {


    companion object {
        val AUTOMATIC = object : DiskCacheStrategy() {
            override fun isDataCacheable(dataSource: DataSource?): Boolean {
                return dataSource == DataSource.REMOTE
            }

            override fun isResourceCacheable(
                isFromAlternateCacheKey: Boolean,
                dataSource: DataSource?,
                encodeStrategy: EncodeStrategy?
            ): Boolean {
                return (encodeStrategy === EncodeStrategy.TRANSFORMED) && ((isFromAlternateCacheKey && dataSource === DataSource.DATA_DISK_CACHE) || dataSource === DataSource.LOCAL)
            }

            override fun decodeCachedResource(): Boolean {
                return true
            }

            override fun decodeCachedData(): Boolean {
                return true

            }
        }

        val RESOURCE =object : DiskCacheStrategy() {
            override fun isDataCacheable(dataSource: DataSource?): Boolean {
                return false
            }

            override fun isResourceCacheable(
                isFromAlternateCacheKey: Boolean,
                dataSource: DataSource?,
                encodeStrategy: EncodeStrategy?
            ): Boolean {
                return dataSource!=DataSource.RESOURCE_DISK_CACHE && dataSource!=DataSource.MEMORY_CACHE
            }

            override fun decodeCachedResource(): Boolean {
                return true
            }

            override fun decodeCachedData(): Boolean {
                return false
            }

        }
    }

    abstract fun isDataCacheable(dataSource: DataSource?): Boolean
    abstract fun isResourceCacheable(
        isFromAlternateCacheKey: Boolean,
        dataSource: DataSource?,
        encodeStrategy: EncodeStrategy?
    ): Boolean

    abstract fun decodeCachedResource(): Boolean
    abstract fun decodeCachedData(): Boolean
}