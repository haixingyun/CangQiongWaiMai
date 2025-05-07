package com.sunnyweather.changqiongwaimai.data.model


/**
 * 统一返回结果
 */
data class ResponseData<T>(
    val code: Int,
    val msg: String?,
    val data: T?
)