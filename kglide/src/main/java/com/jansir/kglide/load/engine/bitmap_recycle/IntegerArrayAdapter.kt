package com.jansir.kglide.load.engine.bitmap_recycle

class IntegerArrayAdapter :ArrayAdapterInterface<IntArray> {
  companion object{
      private const val TAG = "IntegerArrayPool"
  }
    override fun getTag(): String {
        return TAG
    }

    override fun getArrayLength(array: IntArray): Int {
        return array.size
    }

    override fun newArray(length: Int): IntArray {
        return IntArray(length)
    }

    override fun getElementSizeInBytes(): Int {
        return 4
    }
}