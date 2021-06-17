package com.jansir.kglide.load.engine.bitmap_recycle

import androidx.annotation.VisibleForTesting
import java.util.*

class LruArrayPool(private var maxSize: Int = DEFAULT_SIZE) : ArrayPool {

    companion object {
        private const val DEFAULT_SIZE = 4 * 1024 * 1024

        @VisibleForTesting
        val MAX_OVER_SIZE_MULTIPLE = 8

        /** Used to calculate the maximum % of the total pool size a single byte array may consume.  */
        private const val SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2

    }

    private val groupedMap = GroupedLinkedMap<Key, Any>()
    private val sortedSizes: HashMap<Class<*>, NavigableMap<Int, Int>> =
        HashMap()

    private val adapters = hashMapOf<Class<*>, ArrayAdapterInterface<*>>()
    private var currentSize = 0
    private val keyPool = KeyPool()

    override fun <T : Any> put(array: T, arrayClass: Class<T>) {
        put(array)
    }

    override fun <T : Any> get(size: Int, arrayClass: Class<T>): T {
        val key = keyPool.get(size, arrayClass);
        return getForKey(key,arrayClass)
    }

    override fun <T : Any> getExact(size: Int, arrayClass: Class<T>): T {
        val key = keyPool.get(size, arrayClass);
      return getForKey(key, arrayClass);
    }

    private fun <T> getForKey(key: Key, arrayClass: Class<T>): T {
        val arrayAdapter = getAdapterFromType(arrayClass);
        var result = groupedMap.get(key) as T?
        if (result !=null){
            currentSize-=arrayAdapter.getArrayLength(result)* arrayAdapter.getElementSizeInBytes();
            decrementArrayOfSize(arrayAdapter.getArrayLength(result), arrayClass)
        }
        if (result ==null){
            result = arrayAdapter.newArray(key.size)
        }
        return result!!
    }

    private fun  decrementArrayOfSize(size: Int, arrayClass: Class<*>) {
        val sizes = getSizesForAdapter(arrayClass);
        val current = sizes.get(size)
        if (current == 1) {
            sizes.remove(size)
        } else {
            sizes[size] = current!! - 1
        }
    }


    override fun <T : Any> put(array: T) {
        val arrayClass = array.javaClass
        val arrayAdapter = getAdapterFromType(arrayClass)
        val size = arrayAdapter.getArrayLength(array)
        val arrayBytes = size * arrayAdapter.getElementSizeInBytes()
        //如果arrayBytes大于maxSize/2则直接返回
        if (!isSmallEnoughForReuse(arrayBytes)) {
            return
        }
        val key = keyPool.get(size, arrayClass);
        groupedMap.put(key, array)
        //
        val sizes: NavigableMap<Int, Int> = getSizesForAdapter(arrayClass)
        val current = sizes[key.size]
        sizes[key.size] = if (current == null) 1 else current + 1
        //
        currentSize += arrayBytes
        evict()
    }


    private fun getSizesForAdapter(arrayClass: Class<*>): NavigableMap<Int, Int> {
        var sizes: NavigableMap<Int, Int>? = sortedSizes.get(arrayClass)
        if (sizes == null) {
            sizes = TreeMap()
            sortedSizes.put(arrayClass, sizes)
        }
        return sizes
    }

    private fun isSmallEnoughForReuse(byteSize: Int): Boolean {
        return byteSize <= maxSize / SINGLE_ARRAY_MAX_SIZE_DIVISOR
    }

    private fun <T> getAdapterFromType(arrayPoolClass: Class<T>): ArrayAdapterInterface<T> {
        var adapter: ArrayAdapterInterface<*>? = adapters[arrayPoolClass]
        if (adapter != null) return adapter as ArrayAdapterInterface<T>
        if (arrayPoolClass == ByteArray::class.java) {
            adapter = ByteArrayAdapter()
        } else if (arrayPoolClass == IntArray::class.java) {
            adapter = IntegerArrayAdapter()
        } else {
            throw IllegalArgumentException(
                "No array pool found for: " + arrayPoolClass.simpleName
            )
        }
        adapters[arrayPoolClass] = adapter
        return (adapter as ArrayAdapterInterface<T>)
    }



    override fun clearMemory() {
    }

    override fun trimMemory(level: Int) {
    }

    class Key(val pool: KeyPool) : Poolable {

        var size = 0
        private var arrayClass: Class<*>? = null

        override fun offer() {
            pool.offer(this)
        }

        fun init(length: Int, arrayClass: Class<*>) {
            this.size = length
            this.arrayClass = arrayClass
        }

        override fun equals(other: Any?): Boolean {
            if (other is Key) {
                return (size == other.size) && (arrayClass == other.arrayClass)
            }
            return false
        }

        override fun hashCode(): Int {
            var result = size
            result = 31 * result + (arrayClass?.hashCode() ?: 0)
            return result
        }
    }

    private fun evict() {
        evictToSize(maxSize)
    }

    private fun evictToSize(size: Int) {
        while (currentSize>size){
            val  evicted = groupedMap.removeLast();
            val arrayAdapter = getAdapterFromType(evicted!!::class.java) as ArrayAdapterInterface<Any>
            currentSize -= arrayAdapter.getArrayLength(evicted) * arrayAdapter.getElementSizeInBytes()
            decrementArrayOfSize(arrayAdapter.getArrayLength(evicted), evicted.javaClass)
        }
    }

    class KeyPool : BaseKeyPool<Key>() {

        operator fun get(size: Int, arrayClass: Class<*>): Key {
            val result = get()
            result.init(size, arrayClass)
            return result
        }

        override fun create(): Key {
            return Key(this)
        }

    }
}