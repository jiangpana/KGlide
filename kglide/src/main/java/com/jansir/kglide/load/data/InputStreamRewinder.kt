package com.jansir.kglide.load.data

import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import com.jansir.kglide.load.resource.bitmap.RecyclableBufferedInputStream
import java.io.InputStream

class InputStreamRewinder(
    val `is`: InputStream,
    val byteArrayPool: ArrayPool
) : DataRewinder<InputStream> {

    companion object {
        //5mb
        const val MARK_READ_LIMIT = 5 * 1024 * 1024
    }

    // We don't check is.markSupported() here because RecyclableBufferedInputStream allows resetting
    // after exceeding MARK_READ_LIMIT, which other InputStreams don't guarantee.
    private var bufferedStream = RecyclableBufferedInputStream(`is`, byteArrayPool)

    init {
        bufferedStream.mark(MARK_READ_LIMIT)
    }

    override fun rewindAndGet(): InputStream {
        bufferedStream.reset()
        return bufferedStream
    }

    override fun cleanup() {
        bufferedStream.release()
    }

    fun fixMarkLimits() {
        bufferedStream.fixMarkLimit()
    }

    class Factory(val byteArrayPool: ArrayPool) : DataRewinder.Factory<InputStream> {
        override fun build(data: InputStream): DataRewinder<InputStream> =
            InputStreamRewinder(data, byteArrayPool);

        override fun getDataClass(): Class<InputStream> = InputStream::class.java

    }
}