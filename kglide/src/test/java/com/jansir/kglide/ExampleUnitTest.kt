package com.jansir.kglide

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        for (i in 0..100){
            printThis((0..1).random().toString())
        }
    }

}
fun Any.printThis(message: String) {
    println("${javaClass.simpleName} -> $message")
}