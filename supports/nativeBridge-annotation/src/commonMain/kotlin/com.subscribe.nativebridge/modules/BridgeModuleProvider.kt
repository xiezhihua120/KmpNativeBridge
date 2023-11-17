package com.subscribe.nativebridge.modules

/**
 * Created on 2023/9/25
 * @author：xiezh
 * @function：模块提供器
 */
interface BridgeModuleProvider {

    val modules: MutableMap<String, BridgeModule>

    fun initModules()

    /**
     * 获取JSBridgeModule模块
     */
    fun getModule(module: String): BridgeModule
}