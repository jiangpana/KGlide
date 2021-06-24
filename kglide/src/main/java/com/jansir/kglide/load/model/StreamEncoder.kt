package com.jansir.kglide.load.model

import com.jansir.kglide.load.Encoder
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.engine.bitmap_recycle.ArrayPool
import java.io.*

class StreamEncoder(val byteArrayPool: ArrayPool) : Encoder<InputStream> {

    //todo encode File
    override fun encode(data: InputStream, file: File, options: Options): Boolean {
        val buffer = byteArrayPool[ArrayPool.STANDARD_BUFFER_SIZE_BYTES, ByteArray::class.java]
        val os: OutputStream
        var success = false
        try {
            os = FileOutputStream(file)
            var read = 0
            while ((data.read(buffer)).also { read = it } != -1) {
                os.write(buffer, 0, read)
            }
            os.close()
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            byteArrayPool.put(buffer)
        }
        return success
    }
}