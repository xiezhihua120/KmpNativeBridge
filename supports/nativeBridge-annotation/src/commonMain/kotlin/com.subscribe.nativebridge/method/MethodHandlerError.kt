package com.subscribe.nativebridge.method

/**
 * Created on 2023/11/09
 * @author：xiezh
 * @function：默认错误方法处理器
 */
object MethodHandlerError : MethodHandlerBase() {

    override val method: String = "MethodHandlerError"

    override fun handle(reqId: String, module: String, method: String, params: ByteArray) {
        println("${MethodHandler.TAG} report: didn't find method [${method}]")
    }
}