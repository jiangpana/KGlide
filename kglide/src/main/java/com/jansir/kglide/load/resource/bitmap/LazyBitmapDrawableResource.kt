package com.jansir.kglide.load.resource.bitmap

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.jansir.kglide.load.engine.Resource

class LazyBitmapDrawableResource(
    val resources: Resources,
    val bitmapResource: Resource<Bitmap>
) : Resource<BitmapDrawable> {

  companion object{
      fun obtain(
          resources: Resources, bitmapResource: Resource<Bitmap>?
      ): Resource<BitmapDrawable>? {
          return bitmapResource?.let { LazyBitmapDrawableResource(resources, it) }
      }
  }

    override fun getResourceClass(): Class<BitmapDrawable> {
        return BitmapDrawable::class.java
    }

    override fun get(): BitmapDrawable {
        return BitmapDrawable(resources, bitmapResource.get())
    }

    override fun getSize(): Int {
        return bitmapResource.getSize();
    }

    override fun recycle() {
        bitmapResource.recycle()
    }
}