package com.subscribe.nativebridge

import com.subscribe.nativebridge.method.MethodHandler
import com.subscribe.nativebridge.modules.JSBridgeModuleFactory
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.toCValues
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

/**
 * Created on 2023/11/10
 * @author：xzh
 * @function：Native Bridge Api
 */
@OptIn(ExperimentalForeignApi::class)
@kotlinx.coroutines.ExperimentalCoroutinesApi
object NativeBridgeApi : NativeBridge, SynchronizedObject() {

    private const val TAG = "JSBridgeApi"
    private const val DEBUG = false

    private val methodCallback = MutableStateFlow<MethodCallback?>(null)
    private val eventCallback = MutableStateFlow<EventCallback?>(null)
    private val sendScope = CoroutineScope(newSingleThreadContext("sendContext") + Job())
    private val recvScope = CoroutineScope(newSingleThreadContext("recvContext") + Job())

    init {
        synchronized(this) {
            println("$TAG: NativeBridgeApi init")
            // JSBridgeModuleFactory.initModules()
            initMethodReturn()
            initEventSend()
        }
    }

    /**
     * register method callback ptr
     */
    override fun registerMethodCallback(callback: MethodCallbackPtr) {
        synchronized(this) {
            println("$TAG: registerMethodCallback")
            methodCallback.update {
                { reqId, module, method, pbBytes, size ->
                    callback(reqId, module, method, pbBytes, size)
                }
            }
        }
    }

    /**
     * register event callback ptr
     */
    override fun registerEventCallback(callback: EventCallbackPtr) {
        synchronized(this) {
            println("$TAG: registerEventCallback")
            eventCallback.update {
                { module, method, pbBytes, size ->
                    callback(module, method, pbBytes, size)
                }
            }
        }
    }

    /**
     * receive method request
     */
    override fun receiveMethod(
        reqId: String,
        module: String,
        method: String,
        pbBytes: CPointer<ByteVar>?,
        size: Int
    ) {
        if (DEBUG) {
            println("$TAG: sendJsRequest: $reqId, $module, $method, $size")
        }
        val bridgeModule = JSBridgeModuleFactory.getModule(module)
        val methodHandler = bridgeModule.getMethodHandler(method)

        val params = pbBytes?.readBytes(size) ?: ByteArray(0)
        val scope = if (bridgeModule.enableRecvThread) recvScope else null
        launchCoroutine(scope) {
            methodHandler.handle(reqId, module, method, params)
        }
    }

    private fun initMethodReturn() {
        BridgeCenter.registerMethodReturnListener { reqId, module, method, data ->
            if (DEBUG) {
                println("$TAG method return: $reqId, $module, $method, ${data.size}")
            }
            val bridgeModule = JSBridgeModuleFactory.getModule(module)
            val scope = if (bridgeModule.enableSendThread) sendScope else null
            launchCoroutine(scope) {
                memScoped {
                    methodCallback.value?.invoke(
                        reqId.cstr,
                        module.cstr,
                        method.cstr,
                        data.toCValues().getPointer(this),
                        data.size
                    )
                }
            }
        }
    }

    private fun initEventSend() {
        BridgeCenter.registerEventSendListener { reqId, module, method, data ->
            if (DEBUG) {
                println("$TAG method return: $reqId, $module, $method, ${data.size}")
            }
            val bridgeModule = JSBridgeModuleFactory.getModule(module)
            val scope = if (bridgeModule.enableSendThread) sendScope else null
            launchCoroutine(scope) {
                memScoped {
                    eventCallback.value?.invoke(
                        module.cstr,
                        method.cstr,
                        data.toCValues().getPointer(this),
                        data.size
                    )
                }
            }
        }
    }

    private inline fun launchCoroutine(scope: CoroutineScope?, crossinline block: () -> Unit) {
        if (scope != null) {
            scope.launch { block.invoke() }
        } else {
            block.invoke()
        }
    }
}