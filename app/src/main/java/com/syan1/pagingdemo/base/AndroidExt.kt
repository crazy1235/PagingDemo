package com.syan1.pagingdemo.base

import kotlin.math.roundToInt

fun <A, B> Pair<A, B>.swap(): Pair<B, A> {
    return this.second to this.first
}

infix fun <A, B, C> Pair<A, B>.to(third: C): Triple<A, B, C> {
    return Triple(first, second, third)
}

fun Boolean.whenTrue(block: (Boolean) -> Unit): Boolean {
    if (this) block(this)
    return this
}

fun Boolean.whenFalse(block: (Boolean) -> Unit): Boolean {
    if (!this) block(this)
    return this
}

fun <T> Collection<T>.takePercent(percentage: Double): List<T> {
    require(percentage in 0.0..1.0) { "a floating point percentage is required! " }
    return this.take((this.size * percentage).roundToInt())
}

fun main() {
    val result = Pair("abc", 123).swap()
    println(result)

    val pairResult = "123" to "abc"
    println(pairResult)

    val tripleResult = "123" to "abc" to 123
    println(tripleResult)

    true.whenTrue {
        println(tripleResult)
    }
}