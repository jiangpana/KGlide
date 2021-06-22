package com.jansir.kglide.load.data

import android.text.TextUtils
import com.jansir.kglide.Priority
import com.jansir.kglide.load.DataSource
import com.jansir.kglide.load.model.KGlideUrl
import com.jansir.kglide.util.ContentLengthInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.properties.Delegates


class HttpUrlFetcher(val glideUrl: KGlideUrl) : DataFetcher<InputStream> {

    companion object {
        const val MAXIMUM_REDIRECTS = 5
        const val DEFAULT_TIME_OUT = 2500
        const val TAG="HttpUrlFetcher"
    }

    private var isCancelled: Boolean=false
    private var stream: InputStream? = null
    private val connectionFactory = DefaultHttpUrlConnectionFactory()
    private var urlConnection by Delegates.notNull<HttpURLConnection>()
    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        println("HttpUrlFetcher priority")
        try {
            val result = loadDataWithRedirects(URL(glideUrl.url), 0, null, emptyMap())
            result?.let {
                callback.onDataReady(it)
            }
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        } finally {
        }
    }

    private fun loadDataWithRedirects(
        url: URL, redirects: Int, lastUrl: URL?,
        headers: Map<String, String>
    ): InputStream? {
        check(redirects < MAXIMUM_REDIRECTS){"Too many (> \" + $MAXIMUM_REDIRECTS + \") redirects!"}
        urlConnection = connectionFactory.build(url)
        // 添加头部
        for ((key, value) in headers) {
            urlConnection.addRequestProperty(key, value)
        }

        urlConnection.connectTimeout = DEFAULT_TIME_OUT
        urlConnection.readTimeout = DEFAULT_TIME_OUT
        urlConnection.useCaches = false
        urlConnection.doInput = true

        // Stop the urlConnection instance of HttpUrlConnection from following redirects so that
        // redirects will be handled by recursive calls to this method, loadDataWithRedirects.
        urlConnection.instanceFollowRedirects = false

        // Connect explicitly to avoid errors in decoders if connection fails.
        urlConnection.connect()
        // Set the stream so that it's closed in cleanup to avoid resource leaks. See #2352.
        stream = urlConnection.inputStream
        if (isCancelled) {
            return null
        }
        val statusCode = urlConnection.responseCode;
        if (isHttpOk(statusCode)) {
            return getStreamForSuccessfulRequest(urlConnection)
        }else if (isHttpRedirect(statusCode)){
            println("$TAG  statusCode =300  ")
            //300 重定向
            val redirectUrlString = urlConnection.getHeaderField("Location")
            check(redirectUrlString.isNotBlank()){
                "Received empty or null redirect url"
            }
            val redirectUrl = URL(url, redirectUrlString)
            // Closing the stream specifically is required to avoid leaking ResponseBodys in addition
            // to disconnecting the url connection below. See #2352.
            cleanup()
            return loadDataWithRedirects(redirectUrl, redirects + 1, url, headers)
        }else{
            throw Exception(urlConnection.responseMessage + "statusCode =$statusCode")
        }

    }

    private fun isHttpRedirect(statusCode: Int): Boolean {
        return statusCode / 100 == 3
    }

    private fun getStreamForSuccessfulRequest(urlConnection: HttpURLConnection): InputStream? {
       println("urlConnection.contentLength =${ urlConnection.contentLength}")
        if (TextUtils.isEmpty(urlConnection.contentEncoding)) {
            val contentLength = urlConnection.contentLength
            stream = ContentLengthInputStream.obtain(urlConnection.inputStream, contentLength.toLong())
        }else{
            stream = urlConnection.inputStream
        }
        return stream
    }

    private fun isHttpOk(statusCode: Int): Boolean {
        return statusCode / 100 == 2
    }

    override fun cleanup() {
        stream?.apply {
            try {
                close();
            } catch (e: Exception) {
            }
            urlConnection.disconnect()
        }
    }

    override fun cancel() {
        isCancelled=true
    }

    override fun getDataClass(): Class<InputStream> {
        return InputStream::class.java
    }

    override fun getDataSource(): DataSource {
        return DataSource.REMOTE
    }

    interface HttpUrlConnectionFactory {
        fun build(url: URL): HttpURLConnection
    }

    private class DefaultHttpUrlConnectionFactory : HttpUrlConnectionFactory {
        override fun build(url: URL): HttpURLConnection {
            return url.openConnection() as HttpURLConnection
        }

    }
}