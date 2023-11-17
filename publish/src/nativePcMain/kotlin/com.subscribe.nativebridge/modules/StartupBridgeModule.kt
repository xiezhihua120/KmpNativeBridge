package com.subscribe.nativebridge.modules

import com.subscribe.nativebridge.annotation.Event
import com.subscribe.nativebridge.annotation.Method
import com.subscribe.nativebridge.annotation.Module
import com.subscribe.nativebridge.annotation.Param
import com.subscribe.nativebridge.annotation.Return
import com.subscribe.nativebridge.annotation.MethodReturn
import com.subscribe.nativebridge.event.EventHandlerBase

/**
 * Created on 2023/11/10
 * @author：xzh
 * @function：启动模块
 */
@Module(name = "Startup", enableRecvThread = true, enableSendThread = true)
object StartupBridgeModule {

    @Event("add")
    object EventAdd : EventHandlerBase<String?>()

    @Event("remove")
    object EventRemove : EventHandlerBase<Any?>()

    @Method("init")
    fun init(@Param param: String, @Return ret: MethodReturn<Any?>) {
        EventAdd.sendEvent(null)
    }

    @Method("release")
    fun release(@Param param: Any?, @Return ret: MethodReturn<String>) {
        EventRemove.sendEvent(Any())
    }

}