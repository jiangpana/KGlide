# KGlide
用kotlin语言精简大量代码仿写glide

### 一、前言 
Glide是一个极其著名的Android的快速高效的开源媒体管理和图像加载框架，它将媒体解码、内存和磁盘缓存以及资源池打包成一个简单易用的界面。源码地址 [https://github.com/bumptech/glide](https://github.com/bumptech/glide) 

古人云：“纸上得来终觉浅，绝知此事要躬行”。不动手写只看代码进行分析,印象始终不深刻,不能更进一步了解Glide框架设计精髓.

所以打算使用Kotlin语言进行仿写,GitHub地址[https://github.com/jiangpana/KGlide](https://github.com/jiangpana/KGlide) 

### 二、关键类&作用
##### 核心类
- KGlide : 框架的入口类
- Target: 图片的请求的目标类,负责显示图片
- BaseRequestOptions : 负责请求的相关基础配置
- Options :  负责请求过程配置的封装,用map实现
- Lifecycle: 基于观察者设计模式,负责管理LifecycleListener,实现类有ActivityFragmentLifecycle,ApplicationLifecycle
- FactoryPools : 对象池,复用对象
- GlideExecutor : 线程池,主要有SourceExecutor ,DiskCacheExecutor ,AnimationExecutor ,只有SourceExecutor可以用来网络请求
- RequestManager: 负责管理图片请求,实现LifecycleListener,在activity生命周期回调做相关处理
- SingleRequest : 负责图片请求成功失败取消等相关操作
- Engine : 负责启动启动图片加载任务和内存缓存
- EngineJob : 负责启动DecodeJob ,并处理资源成功失败相关回调
- DecodeJob : 负责从源获取数据,解码,转码,变换,磁盘缓存相关
- DataCacheGenerator : 负则从磁盘缓存获取未解码的data数据
- ResourceCacheGenerator : 负则从磁盘缓存获取已解码可直接使用的resource数据
- SourceGenerator : 负责用来获取解码的源数据
##### Registry
- Registry:注册表. 内部主要有modelLoaderRegistry,decoderRegistry,resourceEncoderRegistry,encoderRegistry等相关注册表,用来解码转码编码等相关功能
- ModelLoaderRegistry : 模型加载表,用于通过model获取data
- ResourceDecoderRegistry : 资源解码表,用于将获取到的data 解码成Resource
- TranscoderRegistry: 转码表,用于将Resource转码,比如bitmap -> bitmapDrawable
- DataRewinderRegistry: 数据回卷表,用于回卷流,便于inputStream重读
- EncoderRegistry: 编码表,用于data磁盘缓存相关
- ResourceEncoderRegistry: 资源编码表,用于Resource磁盘缓存
- DecodeHelper : 负责提供解码编码相关操作帮助类
##### ModelLoader 相关
- ModelLoader :  接口,用于通过model获取data
- ModelLoaderFactory : 用于生产modelLoader
- FileLoader : 处理model为file情况
- StringLoader : 处理model为string的情况
##### 缓存相关
- Key : 负责磁盘缓存的key ,实现类有DataCacheKey,ResourceCacheKey 等
- DiskLruCache : 负责磁盘缓存
- LruArrayPool : 负责数组的缓存,防止内存抖动
- LruBitmapPool : 用来缓存bitmap,复用bitmap ,防止内存抖动
- LruResourceCache: 用来缓存resource ,大小根据cpu核心数计算
- BitmapEncoder : 负责将bitmap编码成file
- StreamEncoder :负责将流编码成file 
- StreamBitmapDecoder : 将流解码成bitmap 

### 三、流程&原理

##### 处理生命周期&封装参数

RequestManagerRetriever#supportFragmentGet方法中 , 构建SupportRequestManagerFragment然后设置RequestManager,并添加到activity中用于监听生命周期
```
 private fun supportFragmentGet(
        context: Context,
        fm: FragmentManager,
        parentHint: Fragment?,
        isParentVisible: Boolean
    ): RequestManager {
        val current = getSupportRequestManagerFragment(fm, parentHint, isParentVisible);
        var requestManager = current.getRequestManager()
        if (requestManager == null) {
            val glide = KGlide.get(context)
            requestManager = factory.build(
                glide, current.getGlideLifecycle(), current.getRequestManagerTreeNode(), context
            )
            current.setRequestManager(requestManager)
        }
        return requestManager
    }
```
BaseRequestOptions#apply中,应用其他BaseRequestOptions的配置

```
 fun  apply(o: BaseRequestOptions<*>): T {
        val other = o
        other.fields.apply {
            if (isSet(SIZE_MULTIPLIER)) {
                sizeMultiplier = other.sizeMultiplier
            }
           // ... 省略大量类似代码
            if (isSet(SIGNATURE)) {
                signature = other.signature
            }
            if (isSet(ONLY_RETRIEVE_FROM_CACHE)) {
                onlyRetrieveFromCache = other.onlyRetrieveFromCache
            }
            if (!isTransformationAllowed) {
                transformations.clear()
                fields.unSet(TRANSFORMATION)
                fields.unSet(TRANSFORMATION_REQUIRED)
                isTransformationRequired = false
                isScaleOnlyOrNoTransform = true
            }
        }
        fields = fields or other.fields
        options.putAll(other.options)
        return self()
    }
```
Options 设置相关option
```

  val CENTER_OUTSIDE: DownsampleStrategy = CenterOutside()
  val FIT_CENTER: DownsampleStrategy = FitCenter()
  val DEFAULT: DownsampleStrategy = CENTER_OUTSIDE
       
  fun downsample(strategy: DownsampleStrategy): T {
        return set(DownsampleStrategy.OPTION, strategy)
    }
    
  open operator fun <Y> set(option: Option<Y>, value: Y): T {
    options[option] = value
    return self()
}
```
##### 构建请求
SingleRequest#obtain方法中构建request , 泛型R 默认为Drawable.  如果是asBitmap()则为Bitmap 

```
  //callbackExecutor为在主线程执行的Executor
  //target 默认为 DrawableImageViewTarget
    fun <R> obtain(
            context: Context,
            glideContext: GlideContext,
            model: Any,
            transcodeClass: Class<R>,
            requestOptions: BaseRequestOptions<*>,
            overrideWidth: Int,
            overrideHeight: Int,
            priority: Priority,
            target: Target<R>,
            targetListener: RequestListener<R>? = null,
            requestListeners: List<RequestListener<R>>? = null,
            requestCoordinator: RequestCoordinator? = null,
            engine: Engine,
            animationFactory: TransitionFactory<R>? = null,
            callbackExecutor: Executor
        ): SingleRequest<R> {
         return SingleRequest()
         ...}
```
##### 开始请求
Engine#waitForExistingOrStartNewJob 方法
```
 private fun <R> waitForExistingOrStartNewJob(
        glideContext: GlideContext,
        model: Any,
        signature: Key,
        width: Int,
        height: Int,
        resourceClass: Class<*>,
        transcodeClass: Class<R>,
        priority: Priority,
        diskCacheStrategy: DiskCacheStrategy,
        transformations: Map<Class<*>, Transformation<*>>,
        isTransformationRequired: Boolean,
        isScaleOnlyOrNoTransform: Boolean,
        options: Options,
        isMemoryCacheable: Boolean,
        useUnlimitedSourceExecutorPool: Boolean,
        useAnimationPool: Boolean,
        onlyRetrieveFromCache: Boolean,
        cb: ResourceCallback,
        callbackExecutor: Executor,
        key: EngineKey,
        startTime: Long
    ): LoadStatus? {
        //先从缓存中获取EngineJob,如果当前任务还在执行则添加回调
        val current: EngineJob<*>? = jobs.get(key, onlyRetrieveFromCache)
        current?.let {
            current.addCallback(cb, callbackExecutor)
            return LoadStatus(current, cb)
        }
        //构建engineJob
        val engineJob = engineJobFactory!!.build<R>(
            key,
            isMemoryCacheable,
            useUnlimitedSourceExecutorPool,
            useAnimationPool,
            onlyRetrieveFromCache
        )
        //构建decodeJob
        val decodeJob = decodeJobFactory!!.build(
            glideContext,
            model,
            key,
            signature,
            width,
            height,
            resourceClass,
            transcodeClass,
            priority,
            diskCacheStrategy,
            transformations,
            isTransformationRequired,
            isScaleOnlyOrNoTransform,
            onlyRetrieveFromCache,
            options,
            engineJob
        )
        //将engineJob缓存起来
        jobs.put(key, engineJob)
        engineJob.addCallback(cb, callbackExecutor)
        //启动decodeJob
        engineJob.start(decodeJob)
        return LoadStatus(engineJob, cb)
    }
```
##### 从源获取数据 data
SourceGenerator#startNext , 如果支持data缓存就处理缓存 

```
 override fun startNext(): Boolean {
        printThis("startNext() " +Thread.currentThread().name)
        //缓存data
        if (dataToCache!=null){
            val data: Any = dataToCache!!
            dataToCache = null
            cacheData(data)
        }
        if (sourceCacheGenerator != null && sourceCacheGenerator!!.startNext()) {
            return true
        }
        sourceCacheGenerator = null

        //从源获取data
        loadData = null
        var started = false
        //遍历modelLoader获取data
        while (!started && hasNextModelLoader()) {
            loadData = helper.getLoadData()[loadDataListIndex++]
            loadData?.let {
                if (helper.getDiskCacheStrategy().isDataCacheable(it.fetcher.getDataSource())
                    || helper.hasLoadPath(it.fetcher.getDataClass())
                ) {
                    started = true
                    startNextLoad(it)
                }
            }
        }
        return started
    }
```
HttpUrlFetcher#loadData ,通过HttpURLConnection 下载图片
```
  urlConnection.connectTimeout = DEFAULT_TIME_OUT
        urlConnection.readTimeout = DEFAULT_TIME_OUT
        urlConnection.useCaches = false
        urlConnection.doInput = true
        urlConnection.instanceFollowRedirects = false
        urlConnection.connect()
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
            cleanup()
            return loadDataWithRedirects(redirectUrl, redirects + 1, url, headers)
        }else{
            throw Exception(urlConnection.responseMessage + "statusCode =$statusCode")
        }
```


##### 对获取到的data进行解码 
StreamBitmapDecoder#decode()
```
 override fun decode(
        source: InputStream,
        width: Int,
        height: Int,
        options: Options
    ): Resource<Bitmap>? {
        printThis(" decode -> width=$width , height=$height")
        var callbacks: Downsampler.DecodeCallbacks?=null
        return downsampler.decode(source  ,width,height,options,callbacks)
    }
```
Downsampler#decode() , 通过inTargetDensity和inDensity 方式减少内存占用然后
```
  fun decode(
        ris: InputStream,
        width: Int,
        height: Int,
        options: Options,
        callbacks: DecodeCallbacks?
    ): Resource<Bitmap>? {
        var bitmap: Bitmap
        val options = BitmapFactory.Options()
        ris.reset()
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(ris,null,options)
        options.inJustDecodeBounds = false;
        val sourceHeight =options.outHeight
        val sourceWidth =options.outWidth
        printThis("sourceHeight =$sourceHeight sourceWidth =$sourceWidth")
        options.inTargetDensity=width
        options.inDensity=sourceWidth
        options.inScaled=true
        //把流回到起点
        ris.reset()
        bitmap = BitmapFactory.decodeStream(ris,null,options)!!
        printThis("bitmap size = ${Util.getBitmapByteSize(bitmap)}")
        return BitmapResource.obtain(bitmap, bitmapPool);
    }
```
##### 变换
DecodeJob#onResourceDecoded
```
 private fun <Z> onResourceDecoded(dataSource: DataSource, decoded: Resource<Z>?): Resource<Z>? {
       /.../
        var transformed = decoded
        var appliedTransformation: Transformation<Z>? = null
        if (dataSource != DataSource.RESOURCE_DISK_CACHE) {
            //RESOURCE_DISK_CACHE ,不需要 transformed
            appliedTransformation = decodeHelper.getTransformation(resourceSubClass as Class<Z>)
            transformed =
                appliedTransformation?.transform(glideContext!!, decoded, width, height) ?: decoded
        }
        /.../
        return result
    }
```

##### 转码

bitmap 转为bitmapDrawable
```
  //BitmapDrawableTranscoder#transcode
 override fun transcode(
        toTranscode: Resource<Bitmap>?,
        options: Options
    ): Resource<BitmapDrawable> {
        printThis("transcode")
        return LazyBitmapDrawableResource.obtain(resources, toTranscode)!!
    }
    
//LazyBitmapDrawableResource#get
 override fun get(): BitmapDrawable {
     return BitmapDrawable(resources, bitmapResource.get())
 }
```


##### 显示到imageview 中

EngineJob#CallResourceReady , 先调用 cb.onResourceReady(engineResource!!, dataSource)然后移除cb
```
  inner class CallResourceReady(val cb: ResourceCallback) : Runnable {
        override fun run() {
            synchronized(cb.getLock()) {
                synchronized(this@EngineJob) {
                    if (cbs.contains(cb)) {
                        // Acquire for this particular callback.
                        engineResource?.acquire()
                        callCallbackOnResourceReady(cb)
                        removeCallback(cb)
                    }
                }
            }
        }

    }
    
    // callCallbackOnResourceReady调用
      cb.onResourceReady(engineResource!!, dataSource)
```
SingleRequest实现ResourceCallback接口  onResourceReady方法中
```
    override fun onResourceReady(resource: Resource<*>, dataSource: DataSource?) {
        target.onResourceReady(resource.get() as R,null)
    }

```
DrawableImageViewTarget#setResource
```
 override fun setResource(resource: Drawable?) {
        view.setImageDrawable(resource)
    }
```

##### Resource缓存
将缓存策略设置为DiskCacheStrategy.RESOURCE 

```
//如果dataSource 不等于RESOURCE_DISK_CACHE并且不等于MEMORY_CACHE则支持Resource缓存
override fun isResourceCacheable(
                isFromAlternateCacheKey: Boolean,
                dataSource: DataSource?,
                encodeStrategy: EncodeStrategy?
            ): Boolean {
                return dataSource!=DataSource.RESOURCE_DISK_CACHE && dataSource!=DataSource.MEMORY_CACHE
            }
```
DecodeJob#onResourceDecoded
```
   if (diskCacheStrategy.isResourceCacheable(
                isFromAlternateCacheKey,
                dataSource,
                encodeStrategy
            )
        ) {
            //构建缓存key
            val key: Key
            when (encodeStrategy) {
                EncodeStrategy.SOURCE -> key = DataCacheKey(currentSourceKey!!, signature!!);
                EncodeStrategy.TRANSFORMED -> key = ResourceCacheKey(
                    decodeHelper.getArrayPool(),
                    currentSourceKey!!,
                    signature!!,
                    width,
                    height,
                    appliedTransformation,
                    resourceSubClass,
                    options!!
                )
                else -> throw IllegalArgumentException("Unknown strategy: $encodeStrategy")
            }

            val lockedResult = LockedResource.obtain(transformed)
            //初始化deferredEncodeManager,用于待会缓存
            deferredEncodeManager.init(key, encoder!!, lockedResult)
            result = lockedResult
        }
```

DecodeJob#notifyEncodeAndRelease
```
    private fun notifyEncodeAndRelease(resource: Resource<R>, dataSource: DataSource) {
        printThis("notifyEncodeAndRelease")
        val result = resource
        notifyComplete(result, dataSource)
        stage = Stage.ENCODE
        try {
            if (deferredEncodeManager.hasResourceToEncode()) {
               //缓存资源
               deferredEncodeManager.encode(diskCacheProvider, options!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        onEncodeComplete()
    }
```

### 三、总结
本文首先解读各主要类的功能以及方法执行流程,然后对框架进行解读.

用kotlin语言精简代码进行仿写, 希望能更加理解glide源码设计精髓,但glide的源码所能获取的营养远不止如此.

每看一遍又会有不一样的理解, 让人受益匪浅,在此对Glide开源工作者表示崇高的敬意和感谢。



