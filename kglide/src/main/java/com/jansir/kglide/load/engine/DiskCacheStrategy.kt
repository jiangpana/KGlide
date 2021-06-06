package com.jansir.kglide.load.engine

import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.EncodeStrategy


abstract class DiskCacheStrategy {


    companion object{
        val AUTOMATIC = object : DiskCacheStrategy() {
            override fun isDataCacheable(dataSource: DataSource?): Boolean {
                return dataSource == DataSource.REMOTE
            }

            override fun isResourceCacheable(
                isFromAlternateCacheKey: Boolean,
                dataSource: DataSource?,
                encodeStrategy: EncodeStrategy?
            ): Boolean {
                return true
            }

            override fun decodeCachedResource(): Boolean {
                return true
            }

            override fun decodeCachedData(): Boolean {
                return true

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