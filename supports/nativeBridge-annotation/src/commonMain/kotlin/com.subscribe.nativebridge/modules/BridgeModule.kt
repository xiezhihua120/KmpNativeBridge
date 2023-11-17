package com.subscribe.nativebridge.modules

import com.subscribe.nativebridge.event.EventHandlerBase
import com.subscribe.nativebridge.event.EventHandlerError
import com.subscribe.nativebridge.method.MethodHandlerBase
import com.subscribe.nativebridge.method.MethodHandlerError

/**
 * Created on 2023/11/10
 * @author：xiezh
 * @function：桥接模块
 */
interface BridgeModule {

    /**
     * 模块名称
     */
    val module: String

    /**
     * 启用发送线程
     */
    val enableSendThread: Boolean

    /**
     * 启用接收线程
     */
    val enableRecvThread: Boolean

    /**
     * 方法处理器
     */
    val methodHandlers: MutableMap<String, MethodHandlerBase>

    /**
     * 事件处理器
     */
    val eventHandlers: MutableMap<String, EventHandlerBase<*>>

    /**
     * 模块初始化
     */
    fun initModule()

    /**
     * 获取方法处理器
     */
    fun getMethodHandler(method: String): MethodHandlerBase {
        return methodHandlers[method] ?: MethodHandlerError
    }

    /**
     * 获取事件处理器
     */
    fun getEventHandler(method: String): EventHandlerBase<*> {
        return eventHandlers[method] ?: EventHandlerError
    }
}