package com.jansir.kglide.load.engine

import androidx.annotation.VisibleForTesting
import com.jansir.kglide.load.Key
import java.util.*

class Jobs {
    private val jobs: HashMap<Key, EngineJob<*>> =
        HashMap()
    private val onlyCacheJobs: HashMap<Key, EngineJob<*>> =
        HashMap()

    fun getAll(): Map<Key, EngineJob<*>?>? {
        return Collections.unmodifiableMap(jobs)
    }

    operator fun get(key: Key, onlyRetrieveFromCache: Boolean): EngineJob<*>? {
        return getJobMap(onlyRetrieveFromCache)[key]
    }

    fun put(key: Key, job: EngineJob<*>) {
        getJobMap(job.onlyRetrieveFromCache())[key] = job
    }

    fun removeIfCurrent(key: Key, expected: EngineJob<*>) {
        val jobMap: MutableMap<Key, EngineJob<*>> =
            getJobMap(expected.onlyRetrieveFromCache())
        if (expected.equals(jobMap[key])) {
            jobMap.remove(key)
        }
    }

    private fun getJobMap(onlyRetrieveFromCache: Boolean): MutableMap<Key, EngineJob<*>> {
        return if (onlyRetrieveFromCache) onlyCacheJobs else jobs
    }
}