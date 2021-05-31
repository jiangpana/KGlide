package com.jansir.kglide.ext

import android.app.Activity


fun Activity.assertNotDestroyed() {
    if(isDestroyed){
        throw IllegalArgumentException("You cannot start a load for a destroyed activity")
    }
}
fun Activity.isActivityVisible() =!isFinishing