package com.jansir.kglide.load.engine.exector

import android.os.Process
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import androidx.annotation.IntRange
import java.util.concurrent.*

class GlideExecutor constructor(val delegate: ExecutorService) : ExecutorService by delegate {

    companion object {
        private val DEFAULT_SOURCE_EXECUTOR_NAME = "source"
        private val DEFAULT_DISK_CACHE_EXECUTOR_NAME = "disk-cache"
        private val DEFAULT_DISK_CACHE_EXECUTOR_THREADS = 1
        private val TAG = "KGlideExecutor"

        //无限线程池的执行程序的默认线程名称前缀，用于加载/解码/转换缓存中未找到的数据
        private val DEFAULT_SOURCE_UNLIMITED_EXECUTOR_NAME = "source-unlimited"
        private val DEFAULT_ANIMATION_EXECUTOR_NAME = "animation"

        /** 线程池中线程的存活时间（以毫秒为单位） */
        private val KEEP_ALIVE_TIME_MS = TimeUnit.SECONDS.toMillis(10)

        // 线程数不要超过四个线程
        private val MAXIMUM_AUTOMATIC_THREAD_COUNT = 4

        // May be accessed on other threads, but this is an optimization only so it's ok if we set its
        // value more than once.
        @Volatile
        var bestThreadCount = 0

        private fun calculateBestThreadCount(): Int {
            if (bestThreadCount == 0) {
                bestThreadCount = Math.min(
                    MAXIMUM_AUTOMATIC_THREAD_COUNT,
                    RuntimeCompat.availableProcessors()
                )
            }
            return bestThreadCount
        }


        /** Shortcut for calling [Builder.build] on [.newAnimationBuilder].  */
        fun newAnimationExecutor(): GlideExecutor? {
            val bestThreadCount = calculateBestThreadCount()
            // We don't want to add a ton of threads running animations in parallel with our source and
            // disk cache executors. Doing so adds unnecessary CPU load and can also dramatically increase
            // our maximum memory usage. Typically one thread is sufficient here, but for higher end devices
            // with more cores, two threads can provide better performance if lots of GIFs are showing at
            // once.
            val maximumPoolSize = if (bestThreadCount >= 4) 2 else 1
            return Builder( /*preventNetworkOperations=*/
                true
            )
                .setThreadCount(maximumPoolSize)
                .setName(DEFAULT_ANIMATION_EXECUTOR_NAME).build()
        }


        /** Shortcut for calling [Builder.build] on [.newDiskCacheBuilder].  */
        fun newDiskCacheExecutor(): GlideExecutor? {
            return Builder( /*preventNetworkOperations=*/
                true
            )
                .setThreadCount(DEFAULT_DISK_CACHE_EXECUTOR_THREADS)
                .setName(DEFAULT_DISK_CACHE_EXECUTOR_NAME).build()
        }


        /** Shortcut for calling [Builder.build] on [.newSourceBuilder].  */
        fun newSourceExecutor(): GlideExecutor {
            return   Builder( /*preventNetworkOperations=*/
                false
            )
                .setThreadCount(calculateBestThreadCount())
                .setName(DEFAULT_SOURCE_EXECUTOR_NAME)
                .build()
        }

        fun newUnlimitedSourceExecutor(): GlideExecutor {
            return GlideExecutor(
                ThreadPoolExecutor(
                    0, Int.MAX_VALUE,
                    KEEP_ALIVE_TIME_MS,
                    TimeUnit.MILLISECONDS,
                    SynchronousQueue<Runnable>(),
                    DefaultThreadFactory(
                        DEFAULT_SOURCE_UNLIMITED_EXECUTOR_NAME,
                        UncaughtThrowableStrategy.DEFAULT,
                        false
                    )
                )
            )
        }
    }

    override fun shutdown() {
        delegate.shutdown()
    }

    override fun <T : Any?> submit(task: Callable<T>?): Future<T> = delegate.submit(task)

    override fun <T : Any?> submit(task: Runnable?, result: T): Future<T> =
        delegate.submit(task, result)

    override fun submit(task: Runnable?): Future<*> = delegate.submit(task)

    override fun shutdownNow(): MutableList<Runnable> = delegate.shutdownNow()

    override fun isShutdown(): Boolean = delegate.isShutdown

    override fun awaitTermination(timeout: Long, unit: TimeUnit?) =
        delegate.awaitTermination(timeout, unit)

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>?): T =
        delegate.invokeAny(tasks)

    override fun <T : Any?> invokeAny(
        tasks: MutableCollection<out Callable<T>>?,
        timeout: Long,
        unit: TimeUnit?
    ): T = delegate.invokeAny(tasks, timeout, unit)

    override fun isTerminated(): Boolean = delegate.isTerminated

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>?): MutableList<Future<T>> =
        delegate.invokeAll(tasks)

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>?,
        timeout: Long,
        unit: TimeUnit?
    ): MutableList<Future<T>> = delegate.invokeAll(tasks, timeout, unit)

    override fun execute(command: Runnable?) {
        delegate.execute(command)
    }

    override fun toString(): String = delegate.toString()

    private class DefaultThreadFactory(
        val name: String,
        val uncaughtThrowableStrategy: UncaughtThrowableStrategy,
        val preventNetworkOperations: Boolean
    ) : ThreadFactory {
        private val DEFAULT_PRIORITY = (Process.THREAD_PRIORITY_BACKGROUND
                + Process.THREAD_PRIORITY_MORE_FAVORABLE)
        private var threadNum = 0
        override fun newThread(r: Runnable?): Thread {
            val result = object : Thread(r, "kGlide-  $name  -thread-  $threadNum") {
                override fun run() {
                    // why PMD suppression is needed: https://github.com/pmd/pmd/issues/808

                    // why PMD suppression is needed: https://github.com/pmd/pmd/issues/808
                    Process.setThreadPriority(
                        DEFAULT_PRIORITY
                    )
                    // NOPMD AccessorMethodGeneration
                    if (preventNetworkOperations) {
                        //防止网络操作
                        StrictMode.setThreadPolicy(
                            StrictMode.ThreadPolicy.Builder().detectNetwork()
                                .penaltyDeath().build()
                        )
                    }
                    try {
                        super.run()
                    } catch (t: Throwable) {
                        uncaughtThrowableStrategy.handle(t)
                    }
                }
            }
            threadNum++
            return result
        }

    }

    interface UncaughtThrowableStrategy {
        companion object {
            val LOG = object : UncaughtThrowableStrategy {
                override fun handle(t: Throwable?) {
                    if (t != null && Log.isLoggable(
                            TAG,
                            Log.ERROR
                        )
                    ) {
                        Log.e(TAG, "Request threw uncaught throwable", t)
                    }
                }
            }
            var DEFAULT = LOG
        }

        fun handle(t: Throwable?)
    }

    class Builder(val preventNetworkOperations: Boolean) {


        companion object {
            /**
             * Prevents core and non-core threads from timing out ever if provided to [ ][.setThreadTimeoutMillis].
             */
            val NO_THREAD_TIMEOUT = 0L
        }

        private var corePoolSize = 0
        private var maximumPoolSize = 0

        private lateinit var name: String
        private var threadTimeoutMillis: Long = 0

        private var uncaughtThrowableStrategy: UncaughtThrowableStrategy =
            UncaughtThrowableStrategy.DEFAULT

        fun setThreadTimeoutMillis(threadTimeoutMillis: Long): Builder {
            this.threadTimeoutMillis = threadTimeoutMillis
            return this
        }

        /** Sets the maximum number of threads to use.  */
        fun setThreadCount(@IntRange(from = 1) threadCount: Int): Builder {
            corePoolSize = threadCount
            maximumPoolSize = threadCount
            return this
        }

        fun setUncaughtThrowableStrategy(strategy: UncaughtThrowableStrategy): Builder {
            this.uncaughtThrowableStrategy = strategy
            return this
        }

        /**
         * Sets the prefix to use for each thread name created by any [GlideExecutor]s built by
         * this `Builder`.
         */
        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun build(): GlideExecutor {
            require(!TextUtils.isEmpty(name)) { "Name must be non-null and non-empty, but given: $name" }
            val executor =
                ThreadPoolExecutor(
                    corePoolSize,
                    maximumPoolSize,  /*keepAliveTime=*/
                    threadTimeoutMillis,
                    TimeUnit.MILLISECONDS,
                    PriorityBlockingQueue<Runnable>(),
                    DefaultThreadFactory(
                        name,
                        uncaughtThrowableStrategy,
                        preventNetworkOperations
                    )
                )
            if (threadTimeoutMillis != NO_THREAD_TIMEOUT) {
                executor.allowCoreThreadTimeOut(true)
            }
            return GlideExecutor(executor)
        }
    }
}