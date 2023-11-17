package com.subscribe.nativebridge.annotation

/**
 * 模块定义
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Module(
    val name: String,
    val enableRecvThread: Boolean = true,
    val enableSendThread: Boolean = true,
)

/**
 * 事件定义
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Event(val name: String)

/**
 * 方法定义
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Method(val name: String)

/**
 * 方法入参定义
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Param

/**
 * 方法返回定义
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Return