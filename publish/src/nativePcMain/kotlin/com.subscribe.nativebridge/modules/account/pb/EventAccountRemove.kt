package com.subscribe.nativebridge.modules.account.pb

import kotlinx.serialization.Serializable

/**
 * Created on 2023/11/13
 * @author：xzh
 * @function：
 */
@Serializable
data class EventAccountRemove(
    val userList: List<String>
)