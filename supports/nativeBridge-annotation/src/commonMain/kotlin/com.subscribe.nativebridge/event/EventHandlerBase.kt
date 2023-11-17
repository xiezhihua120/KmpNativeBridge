package com.subscribe.nativebridge.event

import com.subscribe.nativebridge.BridgeCenter

/**
 * Created on 2023/9/26
 * @author：xiezh
 * @function：事件发送器
 */
open class EventHandlerBase<T> : EventHandler {

    companion object {
        private const val TAG = "EventHandler"
    }

    private var _module: String = "undefine"
    private var _method: String = "undefine"
    override val module: String get() = _module
    override val method: String get() = _method

    fun setModule(module: String): EventHandlerBase<T> {
        this._module = module
        return this
    }

    fun setMethod(method: String): EventHandlerBase<T> {
        this._method = method
        return this
    }

    override fun send(reqId: String, module: String, method: String, event: ByteArray) {
        BridgeCenter.onEventSend(reqId, module, method, event)
    }
}

