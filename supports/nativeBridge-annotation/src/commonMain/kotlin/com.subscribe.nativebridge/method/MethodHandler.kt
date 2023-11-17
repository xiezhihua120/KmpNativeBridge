package com.subscribe.nativebridge.method

/**
 * Created on 2023/11/09
 * @author：xiezh
 * @function：
 */
interface MethodHandler {

    val module: String
    val method: String

    /**
     * 方法处理
     */
    fun handle(
        reqId: String,
        module: String,
        method: String,
        params: ByteArray,
    )

    companion object {
        const val TAG = "MethodHandler"
    }
}