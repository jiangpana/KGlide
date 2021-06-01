package com.jansir.kglide

import android.content.Context


internal class KGlide {

    companion object {
        private var instance: KGlide? = null

        @Synchronized
        fun get(context: Context): KGlide {
            if (instance == null) {
                synchronized(KGlide::class.java) {
                    if (instance == null) {
                        checkAndInitializeGlide(context)
                    }
                }
            }
            return instance!!
        }

        private fun checkAndInitializeGlide(context: Context) {
            val builder = KGlideBuilder()

            instance = builder.build()
        }
    }

}