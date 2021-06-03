package com.jansir.kglide

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import java.io.File
import java.net.URL

interface ModelTypes<T> {


    @CheckResult
    fun load(string: String): T

/*

    @CheckResult
    fun load(bitmap: Bitmap): T

    @CheckResult
    fun load(drawable: Drawable): T

    @CheckResult
    fun load(uri: Uri): T

    @CheckResult
    fun load(file: File): T

    @CheckResult
    fun load(@RawRes @DrawableRes resourceId: Int): T

    @Deprecated("")
    @CheckResult
    fun load(url: URL): T

    @CheckResult
    fun load(model: ByteArray): T

    @CheckResult
    fun load(model: Any): T*/
}