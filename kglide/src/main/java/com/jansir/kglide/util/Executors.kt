package com.jansir.kglide.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class Executors {
    companion object{
        private val MAIN_THREAD_EXECUTOR: Executor =
            object : Executor {
                private val handler = Handler(Looper.getMainLooper())
                override fun execute(command: Runnable) {
                    handler.post(command)
                }
            }


        fun mainThreadExecutor(): Executor {
            return MAIN_THREAD_EXECUTOR
        }
    }

}