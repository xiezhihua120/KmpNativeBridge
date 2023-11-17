package com.subscribe.nativebridge.method

import com.subscribe.nativebridge.BridgeCenter

/**
 * Created on 2023/11/09
 * @author：xiezh
 * @function：方法处理基类
 */
abstract class MethodHandlerBase : MethodHandler {

    private var _module: String = "undefine"
    private var _method: String = "undefine"

    override val module: String get() = _module
    override val method: String get() = _method

    fun setModule(module: String): MethodHandlerBase {
        this._module = module
        return this
    }

    fun setMethod(method: String): MethodHandlerBase {
        this._method = method
        return this
    }
    /**
     * 发送回调
     */
    fun onMethodReturn(
        reqId: String,
        module: String,
        method: String,
        ret: ByteArray,
    ) {
        BridgeCenter.onMethodReturn(reqId, method, method, ret)
    }
}