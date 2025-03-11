package com.sunnyweather.changqiongwaimai.utils

import android.util.Log
import com.sunnyweather.changqiongwaimai.data.model.ResponseData

object ApiHelper {

    /**
     * 通用的安全 API 调用方法
     */
    suspend fun <T> safeApiCall(apiCall: suspend () -> ResponseData<T>): ResponseData<T> {
        return try {
            val response = apiCall()
            Log.d("ApiCall", "返回数据: $response")
            response
        } catch (e: Exception) {
            Log.e("ApiCall", "请求失败: ${e.message}")
            ResponseData(
                code = -1,
                msg = e.message ?: "未知错误",
                data = null
            )
        }
    }
}
