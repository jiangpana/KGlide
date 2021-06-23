package com.jansir.kglide

import com.jansir.kglide.load.Encoder
import com.jansir.kglide.load.ResourceDecoder
import com.jansir.kglide.load.ResourceEncoder
import com.jansir.kglide.load.data.DataRewinder
import com.jansir.kglide.load.data.DataRewinderRegistry
import com.jansir.kglide.load.engine.DecodePath
import com.jansir.kglide.load.engine.LoadPath
import com.jansir.kglide.load.engine.Resource
import com.jansir.kglide.load.engine.resource.transcode.ResourceTranscoder
import com.jansir.kglide.load.model.ModelLoader
import com.jansir.kglide.load.model.ModelLoaderFactory
import com.jansir.kglide.load.model.ModelLoaderRegistry
import com.jansir.kglide.load.resource.transcode.TranscoderRegistry
import com.jansir.kglide.provider.EncoderRegistry
import com.jansir.kglide.provider.ResourceDecoderRegistry
import com.jansir.kglide.provider.ResourceEncoderRegistry
import java.util.*


class Registry {

    companion object {
        const val BUCKET_GIF = "Gif"
        const val BUCKET_BITMAP = "Bitmap"
        const val BUCKET_BITMAP_DRAWABLE = "BitmapDrawable"
    }

    private val modelLoaderRegistry = ModelLoaderRegistry()
    private val decoderRegistry = ResourceDecoderRegistry()
    private val transcoderRegistry = TranscoderRegistry()
    private val dataRewinderRegistry = DataRewinderRegistry()
    private val resourceEncoderRegistry = ResourceEncoderRegistry()
    private val encoderRegistry = EncoderRegistry()

    //model
    fun <Model, Data> append(
        modelClass: Class<Model>,
        dataClass: Class<Data>,
        factory: ModelLoaderFactory<Model, Data>
    ): Registry {
        modelLoaderRegistry.append(modelClass, dataClass, factory)
        return this
    }

    //decode
    fun <Data, TResource> append(
        bucket: String,
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        decoder: ResourceDecoder<Data, TResource>
    ): Registry {
        decoderRegistry.append(bucket, decoder, dataClass, resourceClass)
        return this
    }

    //transcode
    fun <TResource, Transcode> register(
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>,
        transcoder: ResourceTranscoder<TResource, Transcode>
    ): Registry {
        transcoderRegistry.register(resourceClass, transcodeClass, transcoder)
        return this
    }

    //dataRewinder
    fun register(factory: DataRewinder.Factory<*>): Registry {
        dataRewinderRegistry.register(factory)
        return this
    }

    //encoder
    fun <TResource>append(resourceClass: Class<TResource>, encoder: ResourceEncoder<TResource>): Registry{
        resourceEncoderRegistry.append(resourceClass,encoder)
        return this
    }
    //source encoder
    fun <Data>append(dataClass: Class<Data>, encoder: Encoder<Data>): Registry{
        encoderRegistry.append(dataClass,encoder)
        return this
    }

    fun <Model : Any> getModelLoaders(model: Model): List<ModelLoader<Model, *>> {
        return modelLoaderRegistry.getModelLoaders(model)
    }

    fun <Data, TResource, Transcode> getLoadPath(
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>
    ): LoadPath<Data, TResource, Transcode>? {
        var result: LoadPath<Data, TResource, Transcode>? = null
        val decodePaths = getDecodePaths(dataClass, resourceClass, transcodeClass);
        if (decodePaths.isNotEmpty()) {
            println("getLoadPath -> decodePaths size =${decodePaths.size}")
            result = LoadPath(dataClass, resourceClass, transcodeClass, decodePaths)
        }
        return result
    }

    private fun <Data, TResource, Transcode> getDecodePaths(
        dataClass: Class<Data>,
        resourceClass: Class<TResource>,
        transcodeClass: Class<Transcode>
    ): List<DecodePath<Data, TResource, Transcode>> {
        val decodePaths =
            ArrayList<DecodePath<Data, TResource, Transcode>>()
        //只要dataClass为inputStream就行,此处返回size =2 ,resourceClass为 bitmap 和bitmapDrawable
        val registeredResourceClasses =
            decoderRegistry.getResourceClasses(dataClass, resourceClass);
        registeredResourceClasses.forEach { registeredResourceClass ->
            //registeredResourceClass为,bitmap 和bitmapDrawable
            //transcodeClass 为drawable
            val registeredTranscodeClasses =
                transcoderRegistry.getTranscodeClasses(registeredResourceClass, transcodeClass)
            registeredTranscodeClasses.forEach { registeredTranscodeClass ->
                //此处decoder只为为 ,BitmapDrawableDecoder
                val decoders = decoderRegistry.getDecoders(dataClass, registeredResourceClass);
                val transcoder =
                    transcoderRegistry.get(registeredResourceClass, registeredTranscodeClass)
                val path = DecodePath(
                    dataClass,
                    registeredResourceClass,
                    registeredTranscodeClass,
                    decoders,
                    transcoder
                )
                decodePaths.add(path)
            }
        }
        return decodePaths
    }

    fun <Data:Any> getRewinder(data: Data): DataRewinder<Data>{
        return dataRewinderRegistry.build(data)
    }

    fun isResourceEncoderAvailable(resource: Resource<*>): Boolean {
       return resourceEncoderRegistry.get(resource.getResourceClass()) != null
    }

    fun <Z> getResultEncoder(resource: Resource<Z>?): ResourceEncoder<Z>? {
        val resourceEncoder: ResourceEncoder<Z>? =
            resourceEncoderRegistry.get(resource!!.getResourceClass())
        if (resourceEncoder != null) {
            return resourceEncoder
        }
        throw Exception("${resource.getResourceClass()}没有找到相关resourceEncoder去处理!!")
    }


    fun <X > getSourceEncoder(data: X): Encoder<X>{
        val encoder: Encoder<X>? = encoderRegistry.getEncoder((data as Any).javaClass as Class<X>)
        if (encoder != null) {
            return encoder
        }
        throw Exception("没有找到SourceEncoder")
    }
}