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

        val b =Person.Man
        val a =Person.Women
        print("${b==a}")
        when (a) {
            Person.Man  -> print("Man")
            Person.Women  -> print("Women")
        }
    }

}
fun Any.printThis(message: String) {
    println("${javaClass.simpleName} -> $message")
}
enum class Person {
    Women ,
    Man
}