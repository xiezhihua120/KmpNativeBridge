package com.subscribe.nativebridge.modules.account

import com.subscribe.nativebridge.annotation.Event
import com.subscribe.nativebridge.annotation.Method
import com.subscribe.nativebridge.annotation.MethodReturn
import com.subscribe.nativebridge.annotation.Module
import com.subscribe.nativebridge.annotation.Param
import com.subscribe.nativebridge.annotation.Return
import com.subscribe.nativebridge.event.EventHandlerBase
import com.subscribe.nativebridge.modules.account.pb.AccountInitReq
import com.subscribe.nativebridge.modules.account.pb.AccountInitResp
import com.subscribe.nativebridge.modules.account.pb.AccountReleaseReq
import com.subscribe.nativebridge.modules.account.pb.AccountReleaseResp
import com.subscribe.nativebridge.modules.account.pb.EventAccountAdd
import com.subscribe.nativebridge.modules.account.pb.EventAccountRemove
import com.subscribe.nativebridge.modules.sendEvent

/**
 * Created on 2023/11/10
 * @author：xzh
 * @function：账号模块
 */
@Module(name = "Account", enableRecvThread = true, enableSendThread = true)
object AccountBridgeModule {

    /**
     * 事件：账号添加
     */
    @Event("add")
    object EventAdd : EventHandlerBase<EventAccountAdd>()

    /**
     * 事件：账号移除
     */
    @Event("remove")
    object EventRemove : EventHandlerBase<EventAccountRemove>()

    /**
     * 方法：初始化
     */
    @Method("init")
    fun init(
        @Param param: AccountInitReq, @Return ret: MethodReturn<AccountInitResp>
    ) {
        EventAdd.sendEvent(EventAccountAdd(listOf("Jerry")))
    }

    /**
     * 方法：注销
     */
    @Method("release")
    fun release(
        @Param param: AccountReleaseReq, @Return ret: MethodReturn<AccountReleaseResp>
    ) {
        EventRemove.sendEvent(EventAccountRemove(listOf("Jerry")))
    }

}