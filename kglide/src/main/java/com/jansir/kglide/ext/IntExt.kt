package com.jansir.kglide.ext


fun Int.isSet(flag: Int): Boolean {
    return this and flag != 0
}

fun Int.set(flag: Int): Int {
    return this or flag
}

fun Int.unSet(flag: Int): Int {
    return this and flag.inv()
}