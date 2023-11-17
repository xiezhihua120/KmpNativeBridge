package com.subscribe.nativebridge.event

/**
 * Created on 2023/9/26
 * @author：xiezh
 * @function：错误处理
 */
object EventHandlerError : EventHandlerBase<Any>() {
    override val method: String = "innerErrorEvent"
}