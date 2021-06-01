package com.jansir.kglide

import android.content.Context
import android.content.ContextWrapper

class GlideContext(base: Context) : ContextWrapper(base.applicationContext) {
}