package com.jansir.kglide

import org.junit.Test
import java.nio.charset.Charset
import java.security.MessageDigest
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

      val messageDigest =  MessageDigest.getInstance("SHA-256")
        messageDigest.update("ggdsgdfgdyreyyerytyrty".toByteArray(UTF_8))
        messageDigest.update("ggdsgdfgdyreyyerytyrty".toByteArray(UTF_8))
        messageDigest.update("ggdsgdfgdyreyyerytyrty".toByteArray(UTF_8))
        messageDigest.update("ggdsgdfgdyreyyerytyrty".toByteArray(UTF_8))
        println("messageDigest = ${messageDigest.digest().toString(UTF_8)}")
    }

}
fun Any.printThis(message: String) {
    println("${javaClass.simpleName} -> $message")
}
enum class Person {
    Women ,
    Man
}