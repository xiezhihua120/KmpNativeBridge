package com.subscribe.nativebridge.modules.impl

import com.subscribe.nativebridge.event.EventHandlerBase
import com.subscribe.nativebridge.event.EventHandlerError
import com.subscribe.nativebridge.method.MethodHandlerBase
import com.subscribe.nativebridge.method.MethodHandlerError
import com.subscribe.nativebridge.modules.BridgeModule

/**
 * Created on 2023/9/25
 * @author：xiezh
 * @function：默认错误模块
 */
object BridgeModuleError : BridgeModule {

    override val module: String = "BridgeModuleError"
    override val enableSendThread: Boolean = false
    override val enableRecvThread: Boolean = false
    override val methodHandlers: MutableMap<String, MethodHandlerBase> = mutableMapOf()
    override val eventHandlers: MutableMap<String, EventHandlerBase<*>> = mutableMapOf()
    override fun initModule() {
        // empty impl
    }

    override fun getMethodHandler(method: String): MethodHandlerBase {
        return MethodHandlerError
    }

    override fun getEventHandler(method: String): EventHandlerBase<*> {
        return EventHandlerError
    }
}