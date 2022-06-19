package com.shunyank.zerocard

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}