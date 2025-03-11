package com.sunnyweather.changqiongwaimai.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException

open class BaseViewModel : ViewModel() {

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    protected fun handleNetworkException(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: UnknownHostException) {
                _error.value = "网络连接失败，请检查您的网络"
            } catch (e: HttpException) {
                try {
                    // 尝试解析后端返回的错误信息
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                    _error.value = errorResponse.message
                } catch (jsonException: JsonSyntaxException) {
                    _error.value = "服务器错误：${e.code()}"
                }
            } catch (e: JsonSyntaxException) {
                _error.value = "数据解析错误"
            } catch (e: Exception) {
                _error.value = "未知错误：${e.message}"
            }
        }
    }

    // 定义一个数据类来解析后端返回的错误信息
    data class ErrorResponse(val message: String)
}