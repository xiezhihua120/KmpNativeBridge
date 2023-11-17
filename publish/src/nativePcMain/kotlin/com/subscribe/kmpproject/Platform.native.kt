package com.subscribe.kmpproject

class NativePlatform: Platform {
    override val name: String = "platformName"
}

actual fun getPlatform(): Platform = NativePlatform()