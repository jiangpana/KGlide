package com.jansir.kglide.load.model

import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.DataFetcher
import com.jansir.kglide.signature.ObjectKey
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream

class FileLoader<Data>(val opener: FileOpener<Data>) : ModelLoader<File, Data> {
    override fun buildLoadData(
        model: File,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Data>? {
        return ModelLoader.LoadData(
            sourceKey= ObjectKey (model),
            fetcher= FileFetcher(model, opener));
    }
    override fun handles(model: File): Boolean {
        return true
    }


    interface FileOpener<Data> {

        fun open(file: File?): Data
        fun close(data: Data)
        val dataClass: Class<Data>?
    }

    private class FileFetcher<Data>(val file:File ,val opener: FileOpener<Data> ): DataFetcher<Data> {
        private var data: Data? = null
        override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in Data>) {
            try {
                data = opener.open(file)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                callback.onLoadFailed(e)
                return
            }
            callback.onDataReady(data!!)
        }

        override fun cleanup() {
            if (data != null) {
                try {
                    opener.close(data!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Ignored.
                }
            }
        }

        override fun cancel() {
        }

        override fun getDataClass(): Class<Data> {
            return opener.dataClass!!
        }

        override fun getDataSource(): DataSource {
            return DataSource.LOCAL
        }

    }

    class StreamFactory(
        val opener: FileOpener<InputStream> = object : FileOpener<InputStream> {
            override fun open(file: File?): InputStream {
                return FileInputStream(file)
            }

            override fun close(data: InputStream) {
                data.close()
            }

            override val dataClass: Class<InputStream>?
                get() = InputStream::class.java

        }
    ) : ModelLoaderFactory<File, InputStream> {
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<File, InputStream> {
            return FileLoader(opener);
        }
    }
}