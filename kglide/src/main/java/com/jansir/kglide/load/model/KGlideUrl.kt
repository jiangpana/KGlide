package com.jansir.kglide.load.model

import android.net.Uri
import com.jansir.kglide.ext.printThis
import com.jansir.kglide.load.Key
import java.net.URL
import java.security.MessageDigest


class KGlideUrl(val url: String) : Key {

    companion object {
        private const val ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%;$"

    }

    private val headers: Headers? = Headers.DEFAULT
    private var safeUrl: URL? = null

    fun getHeaders(): Map<String, String>{
        return headers!!.getHeaders()
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(url.toByteArray())
    }

    private var safeStringUrl = ""

    fun toURL(): URL {
        return getSafeUrl()
    }


    private fun getSafeUrl(): URL {
        if (safeUrl == null) {
            safeUrl = URL(getSafeStringUrl())
        }
        return safeUrl!!
    }

    private fun getSafeStringUrl(): String? {
        if (safeStringUrl.isBlank()) {
            val unsafeStringUrl = url
            safeStringUrl = Uri.encode(
                unsafeStringUrl,
                ALLOWED_URI_CHARS
            )
        }
        printThis("unsafeStringUrl = $url")
        printThis("safeStringUrl = $safeStringUrl")
        return safeStringUrl
    }

    override fun equals(o: Any?): Boolean {
        if (o is KGlideUrl) {
            return url == o.url && headers == o.headers
        }
        return true
    }



    override fun hashCode(): Int {
        var result = url.hashCode()
        result += 31 * result + headers.hashCode()
        return result
    }
}