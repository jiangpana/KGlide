package com.jansir.kglide.load.model

import android.net.Uri
import com.jansir.kglide.load.Options
import java.io.File
import java.io.InputStream

class StringLoader<Data>(val uriLoader :ModelLoader<Uri, Data>): ModelLoader<String, Data>  {
    override fun buildLoadData(
        model: String,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<Data>? {
        val uri = parseUri(model)
        if (uri==null ||!uriLoader.handles(uri) )return null
        return  uriLoader.buildLoadData(uri, width, height, options);
    }

    private fun parseUri(model :String):Uri?{
        var uri :Uri
        if (model.isEmpty())return  null
        if (model[0] == '/'){
            uri = toFileUri(model)
        }else{
            uri = Uri.parse(model)
            val scheme = uri.scheme
            if (scheme == null) {
                uri = toFileUri(model)
            }
        }
        return uri
    }

    private fun toFileUri(path: String): Uri {
        return Uri.fromFile(File(path))
    }

    override fun handles(model: String): Boolean {
        return true
    }

    class StreamFactory :ModelLoaderFactory<String,InputStream>{
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<String, InputStream> {
        return StringLoader(multiFactory.build(
            Uri::class.java,
            InputStream::class.java))
        }

    }
}