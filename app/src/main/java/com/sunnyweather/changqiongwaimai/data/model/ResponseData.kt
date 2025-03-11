package com.sunnyweather.changqiongwaimai.data.model

data class ResponseData<T>(
    val code: Int,
    val msg: String?,
    val data: T?
)