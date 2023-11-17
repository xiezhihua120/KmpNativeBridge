package com.subscribe.nativebridge.modules.account.pb

import kotlinx.serialization.Serializable

/**
 * Created on 2023/11/13
 * @author：xzh
 * @function：添加账号事件
 */
@Serializable
data class EventAccountAdd(
    val userList: List<String>
)