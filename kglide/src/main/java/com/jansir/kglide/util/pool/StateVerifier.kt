package com.jansir.kglide.util.pool


abstract class StateVerifier private constructor(){
    abstract fun throwIfRecycled()
    abstract fun setRecycled(isRecycled: Boolean)



    companion object{
        private val DEBUG = false
         fun newInstance(): StateVerifier {
            return if (DEBUG) {
               DebugStateVerifier()
            } else {
              DefaultStateVerifier()
            }
        }
    }

    class DefaultStateVerifier: StateVerifier() {
        @Volatile
        private var isReleased = false

        override fun throwIfRecycled() {
            check(!isReleased) { "Already released" }
        }
        override fun setRecycled(isRecycled: Boolean) {
            isReleased=isRecycled
        }

    }

    class DebugStateVerifier: StateVerifier() {
        @Volatile
        private var recycledAtStackTraceException: RuntimeException? = null


        override fun throwIfRecycled() {
            if (recycledAtStackTraceException != null) {
                throw IllegalStateException(
                    "Already released",
                    recycledAtStackTraceException
                )
            }
        }
        override fun setRecycled(isRecycled: Boolean) {
            recycledAtStackTraceException = if (isRecycled) {
                java.lang.RuntimeException("Released")
            } else {
                null
            }
        }

    }
}