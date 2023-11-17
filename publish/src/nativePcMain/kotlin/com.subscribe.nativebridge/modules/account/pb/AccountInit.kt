package com.subscribe.nativebridge.modules.account.pb

import kotlinx.serialization.Serializable

/**
 * Created on 2023/11/13
 * @author：xzh
 * @function：添加账号
 */
@Serializable
data class AccountInitReq(
    val userList: List<String>
)

@Serializable
data class AccountInitResp(
    val code: Int,
    val msg: String,
    val data: Boolean,
)