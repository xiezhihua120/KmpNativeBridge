package com.subscribe.nativebridge.modules.account.pb

import kotlinx.serialization.Serializable

/**
 * Created on 2023/11/13
 * @author：xzh
 * @function：添加账号
 */
@Serializable
data class AccountReleaseReq(
    val userList: List<String>
)

@Serializable
data class AccountReleaseResp(
    val code: Int,
    val msg: String,
    val data: Boolean,
)