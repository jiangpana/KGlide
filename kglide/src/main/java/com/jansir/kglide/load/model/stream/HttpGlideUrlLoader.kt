package com.jansir.kglide.load.model.stream

import com.jansir.kglide.load.Options
import com.jansir.kglide.load.data.HttpUrlFetcher
import com.jansir.kglide.load.model.KGlideUrl
import com.jansir.kglide.load.model.ModelLoader
import com.jansir.kglide.load.model.ModelLoaderFactory
import com.jansir.kglide.load.model.MultiModelLoaderFactory
import java.io.InputStream


class HttpGlideUrlLoader :ModelLoader<KGlideUrl,InputStream> {
    override fun buildLoadData(
        model: KGlideUrl,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream>? {
        return ModelLoader.LoadData(model,fetcher = HttpUrlFetcher(model))
    }

    override fun handles(model: KGlideUrl): Boolean {
      return true
    }

    class Factory :ModelLoaderFactory<KGlideUrl ,InputStream>{
        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<KGlideUrl, InputStream> {
            return HttpGlideUrlLoader()
        }

    }
}