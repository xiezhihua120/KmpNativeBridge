package com.subscribe.nativebridge.event

/**
 * Created on 2023/9/25
 * @author：xiezh
 * @function：事件容器
 */
interface EventHandler {
    /**
     * JS处理模块
     */
    val module: String

    /**
     * JS处理方法
     */
    val method: String

    /**
     * 发送事件
     */
    fun send(
        reqId: String,
        module: String,
        method: String,
        data: ByteArray
    )
}