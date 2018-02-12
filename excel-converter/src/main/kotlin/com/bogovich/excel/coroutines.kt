package com.bogovich.excel

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

fun main(args: Array<String>) = runBlocking<Unit> {
    val channel = Channel<Map<Int, Int>>()
    launch {
        // this might be heavy CPU-consuming computation or async logic, we'll just send five squares
        val mutableListOf = mutableMapOf<Int, Int>()
        for (x in 1..100) {
            mutableListOf[x] = x * x
            println("send + $mutableListOf")
            channel.send(mutableListOf.toMap())
            if (mutableListOf.size > 2) {
                mutableListOf.clear()
            }
        }
    }
    // here we print five received integers:
    var i = 0
    val mutableMapOf = mutableMapOf<Int, Int>()
    repeat(100) {
        if (i % 2 == 0) {
            println("do some staff")
        }
        val list = channel.receive()
        mutableMapOf.putAll(list)
        println(mutableMapOf)
        //delay(1000)
        i++
    }
    println("Done!")
}