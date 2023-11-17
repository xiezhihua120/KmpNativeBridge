package com.subscribe.nativebridge.modules

import com.subscribe.nativebridge.modules.impl.BridgeModuleError

/**
 * Created on 2023/11/10
 * @author：xiezh
 * @function：请求分发模块
 */
object BridgeModuleCenter {
    private val ErrorModule: BridgeModule = BridgeModuleError

    /**
     * 模块初始化
     */
    fun initModules() {
        // JSBridgeModuleFactory.initModules()
    }

    /**
     * 获取模块
     * @param module 模块名称
     * @return 具体模块
     */
    fun getModule(module: String): BridgeModule {
        // return JSBridgeModuleFactory.getModule(module) ?: ErrorModule
        return ErrorModule
    }
}