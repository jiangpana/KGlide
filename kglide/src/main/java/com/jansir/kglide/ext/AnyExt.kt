package com.jansir.kglide.ext

import android.os.Looper


fun Any.isOnMainThread()= Looper.myLooper() == Looper.getMainLooper()