package com.subscribe.nativebridge.annotation

/**
 * 函数返回值
 */
interface MethodReturn<T> {
    /**
     * 方法返回结果
     * @param result 结果值
     */
    fun invoke(result: T)
}