package com.subscribe.kmpproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform