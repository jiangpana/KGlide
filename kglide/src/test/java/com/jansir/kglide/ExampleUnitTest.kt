package com.jansir.kglide

import org.junit.Test
import java.nio.charset.Charset
import java.security.MessageDigest
import java.util.ArrayList
import kotlin.text.Charsets.UTF_8

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

//箫声咽(yè)，秦娥梦断秦楼月。秦楼月，年年柳色，灞陵伤别。
//乐游原上清秋节，咸阳古道音尘绝。音尘绝，西风残照，汉家陵阙。


//舒适圈中思进取.思进取,回首往昔,更进一步